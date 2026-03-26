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
@Suppress("NOTHING_TO_INLINE")
inline fun Any.getLogger(): Logger = getLogger {
    requireNotNull(this::class.simpleName) {
        "Cannot automatically create class logging tag: $this"
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun createLogger(tag: String): Logger = getLogger { tag }

inline fun <reified T> createLogger(): Logger = getLogger {
    requireNotNull(T::class.simpleName) {
        "Cannot automatically create class logging tag: ${T::class.qualifiedName}"
    }
}

@PublishedApi
internal inline val LOGGING_ENABLED get() = BuildConfig.DEBUG

@PublishedApi
internal inline fun getLogger(lazyTag: () -> String): Logger {
    val tag = if (LOGGING_ENABLED) {
        lazyTag()
    } else {
        // Don't bother evaluating the tag if the logging is disabled.
        null
    }
    return Logger(tag)
}

@JvmInline
value class Logger(
    @PublishedApi
    internal val tag: String?
) {

    inline fun d(exception: Throwable? = null, lazyMessage: () -> Any) {
        log(exception, lazyMessage, Log::d)
    }

    inline fun i(exception: Throwable? = null, lazyMessage: () -> Any) {
        log(exception, lazyMessage, Log::i)
    }

    inline fun v(exception: Throwable? = null, lazyMessage: () -> Any) {
        log(exception, lazyMessage, Log::v)
    }

    inline fun w(exception: Throwable? = null, lazyMessage: () -> Any) {
        log(exception, lazyMessage, Log::w)
    }

    inline fun e(exception: Throwable? = null, lazyMessage: () -> Any) {
        log(exception, lazyMessage, Log::e)
    }

    inline fun wtf(exception: Throwable? = null, lazyMessage: () -> Any) {
        log(exception, lazyMessage, Log::wtf)
    }

    @PublishedApi
    internal inline fun log(
        exception: Throwable? = null,
        lazyMessage: () -> Any,
        block: (tag: String?, message: String, exception: Throwable?) -> Unit,
    ) {
        if (LOGGING_ENABLED) {
            block(tag, lazyMessage().toString(), exception)
        }
    }
}
