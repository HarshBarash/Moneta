
package harshbarash.github.moneta.tflite;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.SystemClock;
import android.os.Trace;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import harshbarash.github.moneta.env.Logger;

// Классификатор, специализированный на рпспозновании изображений с помощью TensorFlow Lite.
public abstract class Classifier {
  private static final Logger LOGGER = new Logger();

  /** Тип модели, используемый для классификации. */
  public enum Model {
    FLOAT,
    QUANTIZED,
  }

  //Тип устройства времени выполнения, используемый для выполнения классификации.
  public enum Device {
    CPU,
    NNAPI,
    GPU
  }

  //Количество результатов, отображаемых в пользовательском интерфейсе.
  private static final int MAX_RESULTS = 3;

  //загрузка модели
  private MappedByteBuffer tfliteModel;

  //Размер изображения по оси x.
  private final int imageSizeX;

  //Размер изображения по оси y.
  private final int imageSizeY;

  private GpuDelegate gpuDelegate = null;

  private NnApiDelegate nnApiDelegate = null;

  protected Interpreter tflite;

  private final Interpreter.Options tfliteOptions = new Interpreter.Options();

  private List<String> labels;

  private TensorImage inputImageBuffer;

  private final TensorBuffer outputProbabilityBuffer;

  private final TensorProcessor probabilityProcessor;


  public static Classifier create(Activity activity, Model model, Device device, int numThreads)
      throws IOException {
    if (model == Model.QUANTIZED) {
      return new ClassifierQuantizedMobileNet(activity, device, numThreads);
    } else {
      return new ClassifierFloatMobileNet(activity, device, numThreads);
    }
  }


  public static class Recognition {

    private final String id;

    private final String title;

    private final Float confidence;

    private RectF location;

    public Recognition(
        final String id, final String title, final Float confidence, final RectF location) {
      this.id = id;
      this.title = title;
      this.confidence = confidence;
      this.location = location;
    }

    public String getId() {
      return id;
    }

    public String getTitle() {
      return title;
    }

    public Float getConfidence() {
      return confidence;
    }

    public RectF getLocation() {
      return new RectF(location);
    }

    public void setLocation(RectF location) {
      this.location = location;
    }

    @Override
    public String toString() {
      String resultString = "";
      if (id != null) {
        resultString += "[" + id + "] ";
      }

      if (title != null) {
        resultString += title + " ";
      }

      if (confidence != null) {
        resultString += String.format("(%.1f%%) ", confidence * 100.0f);
      }

      if (location != null) {
        resultString += location + " ";
      }

      return resultString.trim();
    }
  }

  /** Initializes a {@code Classifier}. */
  protected Classifier(Activity activity, Device device, int numThreads) throws IOException {
    tfliteModel = FileUtil.loadMappedFile(activity, getModelPath());
    switch (device) {
      case NNAPI:
        nnApiDelegate = new NnApiDelegate();
        tfliteOptions.addDelegate(nnApiDelegate);
        break;
      case GPU:
        gpuDelegate = new GpuDelegate();
        tfliteOptions.addDelegate(gpuDelegate);
        break;
      case CPU:
        break;
    }
    tfliteOptions.setNumThreads(numThreads);
    tflite = new Interpreter(tfliteModel, tfliteOptions);

    //подгружаем названия
    labels = FileUtil.loadLabels(activity, getLabelPath());

    int imageTensorIndex = 0;
    int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
    imageSizeY = imageShape[1];
    imageSizeX = imageShape[2];
    DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();
    int probabilityTensorIndex = 0;
    int[] probabilityShape =
        tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
    DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

    inputImageBuffer = new TensorImage(imageDataType);

    outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

    probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

    LOGGER.d("Created a Tensorflow Lite Image Classifier.");
  }

  /** Запускает вывод и возвращает результаты классификации */
  public List<Recognition> recognizeImage(final Bitmap bitmap, int sensorOrientation) {
    Trace.beginSection("recognizeImage");

    Trace.beginSection("loadImage");
    long startTimeForLoadImage = SystemClock.uptimeMillis();
    inputImageBuffer = loadImage(bitmap, sensorOrientation);
    long endTimeForLoadImage = SystemClock.uptimeMillis();
    Trace.endSection();
    LOGGER.v("Timecost to load the image: " + (endTimeForLoadImage - startTimeForLoadImage));

    // Запускает вызов вывода.
    Trace.beginSection("runInference");
    long startTimeForReference = SystemClock.uptimeMillis();
    tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());
    long endTimeForReference = SystemClock.uptimeMillis();
    Trace.endSection();
    LOGGER.v("Timecost to run model inference: " + (endTimeForReference - startTimeForReference));

    // Получает карту меток и вероятностей.
    Map<String, Float> labeledProbability =
        new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
            .getMapWithFloatValue();
    Trace.endSection();

    // Получает лучшие результаты.
    return getTopKProbability(labeledProbability);
  }

  /** Закрывает интерпретатор и модель для освобождения ресурсов. */
  public void close() {
    if (tflite != null) {
      tflite.close();
      tflite = null;
    }
    if (gpuDelegate != null) {
      gpuDelegate.close();
      gpuDelegate = null;
    }
    if (nnApiDelegate != null) {
      nnApiDelegate.close();
      nnApiDelegate = null;
    }
    tfliteModel = null;
  }

  /**  Получите размер изображения по оси x */
  public int getImageSizeX() {
    return imageSizeX;
  }

  /** Получите размер изображения по оси y */
  public int getImageSizeY() {
    return imageSizeY;
  }

  private TensorImage loadImage(final Bitmap bitmap, int sensorOrientation) {
    //загрузка Bitmap
    inputImageBuffer.load(bitmap);

// Создает процессор для тензорного изображения.
  int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
    int numRoration = sensorOrientation / 90;
    ImageProcessor imageProcessor =
        new ImageProcessor.Builder()
            .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(new ResizeOp(imageSizeX, imageSizeY, ResizeMethod.NEAREST_NEIGHBOR))
            .add(new Rot90Op(numRoration))
            .add(getPreprocessNormalizeOp())
            .build();
    return imageProcessor.process(inputImageBuffer);
  }

  private static List<Recognition> getTopKProbability(Map<String, Float> labelProb) {
    PriorityQueue<Recognition> pq =
        new PriorityQueue<>(
            MAX_RESULTS,
            new Comparator<Recognition>() {
              @Override
              public int compare(Recognition lhs, Recognition rhs) {

                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
              }
            });

    for (Map.Entry<String, Float> entry : labelProb.entrySet()) {
      pq.add(new Recognition("" + entry.getKey(), entry.getKey(), entry.getValue(), null));
    }

    final ArrayList<Recognition> recognitions = new ArrayList<>();
    int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
    for (int i = 0; i < recognitionsSize; ++i) {
      recognitions.add(pq.poll());
    }
    return recognitions;
  }

  /** Получает имя файла модели, хранящегося в ассетах. */
  protected abstract String getModelPath();

  protected abstract String getLabelPath();

  // Получает тензорооператор для номализации входного изображения при предварительной обработке.
  protected abstract TensorOperator getPreprocessNormalizeOp();

  protected abstract TensorOperator getPostprocessNormalizeOp();
}
