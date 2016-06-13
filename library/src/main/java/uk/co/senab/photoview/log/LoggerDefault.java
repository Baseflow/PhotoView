/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uk.co.senab.photoview.log;

import android.util.Log;

/**
 * Helper class to redirect {@link LogManager#logger} to android {@link Log}
 * for loglevel warnm, error.
 * Additionally info is logged if enabled via {@Ling LoggerDefault#setLogInfo(boolean)} .
 */
public class LoggerDefault implements Logger {

    /** true: loglevel info is also logged */
    private static boolean logInfo = false;

    /** true: loglevel info is also logged */
    public static boolean isLogInfo() {
        return logInfo;
    }

    /** true: loglevel info is also logged */
    public static void setLogInfo(boolean logInfo) {
        LoggerDefault.logInfo = logInfo;
    }

    @Override
    public int v(String tag, String msg) {
        return 0; // no logging at this level
    }

    @Override
    public int v(String tag, String msg, Throwable tr) {
        return 0; // no logging at this level
    }

    @Override
    public int d(String tag, String msg) {
        return 0; // no logging at this level
    }

    @Override
    public int d(String tag, String msg, Throwable tr) {
        return 0; // no logging at this level
    }

    @Override
    public int i(String tag, String msg) {
        return (!logInfo) ? 0 : Log.i(tag, msg);
    }

    @Override
    public int i(String tag, String msg, Throwable tr) {
        return (!logInfo) ? 0 : Log.i(tag, msg, tr);
    }

    @Override
    public int w(String tag, String msg) {
        return Log.w(tag, msg);
    }

    @Override
    public int w(String tag, String msg, Throwable tr) {
        return Log.w(tag, msg, tr);
    }

    @Override
    public int e(String tag, String msg) {
        return Log.e(tag, msg);
    }

    @Override
    public int e(String tag, String msg, Throwable tr) {
        return Log.e(tag, msg, tr);
    }
}
