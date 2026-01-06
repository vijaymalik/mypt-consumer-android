package co.com.mypt.utils

/**
 * This is the RELEASE version of the logger.
 * Its methods are empty and will be optimized away by the compiler.
 */
object PTLog {
    private const val TAG = "PTLog"

    fun v(tag: String = TAG, msg: String) {
        // Intentionally empty
    }

    fun v(tag: String = TAG, msg: String, tr: Throwable) {
        // Intentionally empty
    }

    fun d(tag: String = TAG, msg: String) {
        // Intentionally empty
    }

    fun d(tag: String = TAG, msg: String, tr: Throwable) {
        // Intentionally empty
    }

    fun i(tag: String = TAG, msg: String) {
        // This is intentionally empty for release builds
    }

    fun i(tag: String = TAG, msg: String, tr: Throwable) {
        // Intentionally empty
    }

    fun w(tag: String = TAG, msg: String) {
        // Intentionally empty
    }

    fun w(tag: String = TAG, msg: String, tr: Throwable) {
        // Intentionally empty
    }

    fun w(tag: String = TAG, tr: Throwable) {
        // Intentionally empty
    }

    fun e(tag: String = TAG, msg: String) {
        // Intentionally empty
    }

    fun e(tag: String = TAG, msg: String, tr: Throwable) {
        // Intentionally empty
    }

    fun wtf(tag: String = TAG, msg: String) {
        // Intentionally empty
    }

    fun wtf(tag: String = TAG, tr: Throwable) {
        // Intentionally empty
    }

    fun wtf(tag: String = TAG, msg: String, tr: Throwable) {
        // Intentionally empty
    }
}