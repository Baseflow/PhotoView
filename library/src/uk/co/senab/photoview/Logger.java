package uk.co.senab.photoview;

/**
 * interface for a logger class to replace the static calls to {@link android.util.Log}
 */
public interface Logger {
    /**
     * Send a {@link android.util.Log#VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	int v(String tag, String msg);
	
    /**
     * Send a {@link android.util.Log#VERBOSE} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    int v(String tag, String msg, Throwable tr);
	
    /**
     * Send a {@link android.util.Log#DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    int d(String tag, String msg);
	
    /**
     * Send a {@link android.util.Log#DEBUG} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    int d(String tag, String msg, Throwable tr);

    /**
     * Send an {@link android.util.Log#INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    int i(String tag, String msg);
	
    /**
     * Send a {@link android.util.Log#INFO} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    int i(String tag, String msg, Throwable tr);
	
    /**
     * Send a {@link android.util.Log#WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    int w(String tag, String msg);
	
    /**
     * Send a {@link android.util.Log#WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    int w(String tag, String msg, Throwable tr);
	
    /**
     * Send an {@link android.util.Log#ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    int e(String tag, String msg);
	
    /**
     * Send a {@link android.util.Log#ERROR} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    int e(String tag, String msg, Throwable tr);
}
