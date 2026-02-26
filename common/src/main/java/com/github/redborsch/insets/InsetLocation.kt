package com.github.redborsch.insets

import androidx.annotation.IntDef

@Retention(AnnotationRetention.SOURCE)
@IntDef(InsetLocation.LEFT, InsetLocation.TOP, InsetLocation.RIGHT, InsetLocation.BOTTOM)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.TYPE,
)
annotation class InsetLocation {
    companion object {
        const val LEFT = 1 shl 0
        const val TOP = 1 shl 1
        const val RIGHT = 1 shl 2
        const val BOTTOM = 1 shl 3

        /**
         * For convenience, to avoid prefixing each value. Can be used as follows:
         * ```
         * InsetLocation { LEFT + RIGHT }
         * ```
         */
        inline operator fun invoke(block: Companion.() -> Int): Int {
            return InsetLocation.block()
        }
    }
}

internal fun @InsetLocation Int.apply(
    @InsetLocation flag: Int,
    valueIfSelected: Int,
    valueIfUnselected: Int = 0,
): Int =
    if (this and flag == flag) {
        valueIfSelected
    } else {
        valueIfUnselected
    }
