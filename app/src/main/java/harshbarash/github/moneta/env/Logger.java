package harshbarash.github.moneta.env;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;


public final class Logger {
    private static final String DEFAULT_TAG = "tensorflow";
    private static final int DEFAULT_MIN_LOG_LEVEL = Log.DEBUG;

    // Классы, которые будут игнорироваться при изучении трассировки стека
    private static final Set<String> IGNORED_CLASS_NAMES;

    static {
        IGNORED_CLASS_NAMES = new HashSet<String>(3);
        IGNORED_CLASS_NAMES.add("dalvik.system.VMStack");
        IGNORED_CLASS_NAMES.add("java.lang.Thread");
        IGNORED_CLASS_NAMES.add(Logger.class.getCanonicalName());
    }

    private final String tag;
    private final String messagePrefix;
    private int minLogLevel = DEFAULT_MIN_LOG_LEVEL;

    /**
     * Создает регистратор, используя имя класса в качестве префикса сообщения.     *
     * @param clazz the simple name of this class is used as the message prefix.
     */
    public Logger(final Class<?> clazz) {
        this(clazz.getSimpleName());
    }

    /**
     *
     * @param messagePrefix добавляется к тексту каждого сообщения.
     */
    public Logger(final String messagePrefix) {
        this(DEFAULT_TAG, messagePrefix);
    }


    public Logger(final String tag, final String messagePrefix) {
        this.tag = tag;
        final String prefix = messagePrefix == null ? getCallerSimpleName() : messagePrefix;
        this.messagePrefix = (prefix.length() > 0) ? prefix + ": " : prefix;
    }

    /**
     * Создает регистратор, используя имя класса вызывающего абонента в качестве префикса сообщения.
     */
    public Logger() {
        this(DEFAULT_TAG, null);
    }

    /**
     * Создает регистратор, используя имя класса вызывающего абонента в качестве префикса сообщения.
     */
    public Logger(final int minLogLevel) {
        this(DEFAULT_TAG, null);
        this.minLogLevel = minLogLevel;
    }

    private static String getCallerSimpleName() {
        // Получить текущий стек вызовов, чтобы мы могли извлечь из него класс вызывающего абонента.
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (final StackTraceElement elem : stackTrace) {
            final String className = elem.getClassName();
            if (!IGNORED_CLASS_NAMES.contains(className)) {
            // Нас интересует только простое имя класса, а не полный пакет.
                final String[] classParts = className.split("\\.");
                return classParts[classParts.length - 1];
            }
        }

        return Logger.class.getSimpleName();
    }

    public void setMinLogLevel(final int minLogLevel) {
        this.minLogLevel = minLogLevel;
    }

    public boolean isLoggable(final int logLevel) {
        return logLevel >= minLogLevel || Log.isLoggable(tag, logLevel);
    }

    private String toMessage(final String format, final Object... args) {
        return messagePrefix + (args.length > 0 ? String.format(format, args) : format);
    }

    public void v(final String format, final Object... args) {
        Log.v(tag, toMessage(format, args));

    }

    public void v(final Throwable t, final String format, final Object... args) {

        Log.v(tag, toMessage(format, args), t);

    }

    public void d(final String format, final Object... args) {
        Log.d(tag, toMessage(format, args));
    }

    public void d(final Throwable t, final String format, final Object... args) {
        Log.d(tag, toMessage(format, args), t);

    }

    public void i(final String format, final Object... args) {
        Log.i(tag, toMessage(format, args));
    }

    public void i(final Throwable t, final String format, final Object... args) {

        Log.i(tag, toMessage(format, args), t);

    }

    public void w(final String format, final Object... args) {
        Log.w(tag, toMessage(format, args));
    }

    public void w(final Throwable t, final String format, final Object... args) {
        Log.w(tag, toMessage(format, args), t);

    }

    public void e(final String format, final Object... args) {
        Log.e(tag, toMessage(format, args));
    }

    public void e(final Throwable t, final String format, final Object... args) {
        Log.e(tag, toMessage(format, args), t);
    }
}
