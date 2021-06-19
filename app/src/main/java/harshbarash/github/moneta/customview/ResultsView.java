
package harshbarash.github.moneta.customview;

import java.util.List;

import harshbarash.github.moneta.tflite.Classifier.Recognition;

public interface ResultsView {
  public void setResults(final List<Recognition> results);
}
