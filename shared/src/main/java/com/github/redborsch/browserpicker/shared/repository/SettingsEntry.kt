package com.github.redborsch.browserpicker.shared.repository

import kotlin.math.abs

class SettingsEntry(
    val appPackage: String,
    val visible: Boolean,
    val order: Int,
) {

    init {
        require(order >= 0) {
            "The order cannot be negative as it will break the serialization logic"
        }
        require(order <= MAX_ORDER) {
            "The order cannot exceed $MAX_ORDER"
        }
    }

    fun serialize(): String = buildString {
        append(appPackage)
        append(PACKAGE_SEPARATOR)
        // Avoid 0, which is unsigned
        val orderToSerializer = order + 1
        if (visible) {
            append(orderToSerializer.toString(NUMBER_FORMAT_RADIX))
        } else {
            append((-orderToSerializer).toString(NUMBER_FORMAT_RADIX))
        }
    }

    companion object {
        private const val PACKAGE_SEPARATOR = '|'

        /**
         * Save with the maximum radix to save some space
         */
        private const val NUMBER_FORMAT_RADIX = Character.MAX_RADIX

        const val MAX_ORDER = Int.MAX_VALUE - 1

        fun deserialize(serialized: String): SettingsEntry? {
            val packageSeparatorIndex = serialized.indexOf(PACKAGE_SEPARATOR)
            // Also ignore empty package
            if (packageSeparatorIndex <= 0) {
                return null
            }
            val appPackage = serialized.substring(0, packageSeparatorIndex)
            if (appPackage.isBlank()) {
                return null
            }
            val orderAndVisibility = serialized.substring(packageSeparatorIndex + 1)

            val deserializedOrder = orderAndVisibility.deserializeIntSafe()
            val visible = deserializedOrder >= 0
            val order = abs(deserializedOrder) - 1

            return SettingsEntry(
                appPackage,
                visible,
                order,
            )
        }

        private fun String.deserializeIntSafe(): Int = runCatching {
            toInt(NUMBER_FORMAT_RADIX)
        }.getOrElse {
            // Note: should not be 0 as it will be decreased by 1
            1
        }
    }
}
