package com.github.redborsch.log

import android.util.Log
import com.github.redborsch.BuildConfig

/**
 * Returns the light logger instance for this class, using the class name as the tag.
 *
 * Recommended usage:
 * ```
 * class MyClass {
 *
 *     private val log = getLogger()
 *
 *     init {
 *       log.d {
 *          "MyClass created"
 *       }
 *     }
 * }
 * ```
 * Will not work for anonymous classes or when calling within custom receivers.
 */
fun Any.getLogger(): Logger {
    return if (BuildConfig.DEBUG) {
        val tag = requireNotNull(this::class.simpleName) {
            "Cannot automatically create class logging tag: $this"
        }
        Logger(tag)
    } else {
        // Logging disabled
        Logger(null)
    }
}

@JvmInline
value class Logger(
    private val tag: String?
) {
    fun d(exception: Throwable? = null, lazyMessage: () -> Any) {
        if (tag != null) {
            Log.d(tag, lazyMessage().toString(), exception)
        }
    }

    fun v(exception: Throwable? = null, lazyMessage: () -> Any) {
        if (tag != null) {
            Log.v(tag, lazyMessage().toString(), exception)
        }
    }

    fun w(exception: Throwable? = null, lazyMessage: () -> Any) {
        if (tag != null) {
            Log.w(tag, lazyMessage().toString(), exception)
        }
    }

    fun e(exception: Throwable? = null, lazyMessage: () -> Any) {
        if (tag != null) {
            Log.e(tag, lazyMessage().toString(), exception)
        }
    }
}
