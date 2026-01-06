package co.com.mypt.utils

import android.util.Log

/**
 * This is the DEBUG version of the logger.
 * It will only be included in debug builds.
 */
object PTLog {
    private const val TAG = "PTLog"

    fun v(tag: String = TAG, msg: String) {
        Log.v(tag, msg)
    }

    fun v(tag: String = TAG, msg: String, tr: Throwable) {
        Log.v(tag, msg, tr)
    }

    fun d(tag: String = TAG, msg: String) {
        Log.d(tag, msg)
    }

    fun d(tag: String = TAG, msg: String, tr: Throwable) {
        Log.d(tag, msg, tr)
    }

    fun i(tag: String = TAG, msg: String) {
        Log.i(tag, msg)
    }

    fun i(tag: String = TAG, msg: String, tr: Throwable) {
        Log.i(tag, msg, tr)
    }

    fun w(tag: String = TAG, msg: String) {
        Log.w(tag, msg)
    }

    fun w(tag: String = TAG, msg: String, tr: Throwable) {
        Log.w(tag, msg, tr)
    }

    fun w(tag: String = TAG, tr: Throwable) {
        Log.w(tag, tr)
    }

    fun e(tag: String = TAG, msg: String) {
        Log.e(tag, msg)
    }

    fun e(tag: String = TAG, msg: String, tr: Throwable) {
        Log.e(tag, msg, tr)
    }

    fun wtf(tag: String = TAG, msg: String) {
        Log.wtf(tag, msg)
    }

    fun wtf(tag: String = TAG, tr: Throwable) {
        Log.wtf(tag, tr)
    }

    fun wtf(tag: String = TAG, msg: String, tr: Throwable) {
        Log.wtf(tag, msg, tr)
    }
}