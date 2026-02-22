package com.github.redborsch.browserpicker.shared.repository

data class SettingsEntry(
    val appPackage: String,
    val visible: Boolean,
    val order: Int,
) {
    fun serialize(): String = buildString {
        append(appPackage)
        append(PACKAGE_SEPARATOR)
        append(visible.serialize())
        append(DATA_SEPARATOR)
        append(order)
    }

    companion object {
        private const val PACKAGE_SEPARATOR = '|'
        private const val DATA_SEPARATOR = ','

        private const val INDEX_VISIBILITY = 0
        private const val INDEX_ORDER = 1

        fun deserialize(serialized: String): SettingsEntry {
            val packageSeparatorIndex = serialized.indexOf(PACKAGE_SEPARATOR)
            val appPackage = serialized.substring(0, packageSeparatorIndex)

            var visible = false
            var order = 0

            parseData(serialized, packageSeparatorIndex + 1) { index, value ->
                when (index) {
                    INDEX_VISIBILITY -> visible = value.deserializeBoolean()
                    INDEX_ORDER -> order = value.deserializeIntSafe()
                    else -> return@parseData
                }
            }

            return SettingsEntry(
                appPackage,
                visible,
                order,
            )
        }

        private fun Boolean.serialize(): Char = if (this) '1' else '0'
        private fun String.deserializeBoolean(): Boolean = this == "1"
        private fun String.deserializeIntSafe(): Int = runCatching {
            toInt()
        }.getOrElse { 0 }

        private inline fun parseData(serialized: String, start: Int, onData: (index: Int, value: String) -> Unit) {
            var index = 0
            var lastDataStart = start
            val length = serialized.length
            for (i in start..length) {
                if (i == length || serialized[i] == DATA_SEPARATOR) {
                    val value = serialized.substring(lastDataStart, i)
                    onData(index++, value)
                    lastDataStart = i + 1
                }
            }
        }
    }
}
