package com.github.redborsch.browserpicker.shared.repository

class BrowserListSettings private constructor(
    val entries: List<SettingsEntry>,
) {

    fun serialize(): Set<String> {
        return entries.mapTo(HashSet()) {
            it.serialize()
        }
    }

    class Builder(capacity: Int) {

        private val entries = ArrayList<SettingsEntry>(capacity)

        fun addEntry(entry: SettingsEntry) {
            entries += entry
        }

        fun build() = BrowserListSettings(entries.toList())
    }

    companion object {

        fun empty() = BrowserListSettings(emptyList())

        fun deserialize(serialized: Set<String>): BrowserListSettings {
            return Builder(serialized.size)
                .apply {
                    serialized.forEach {
                        val entry = SettingsEntry.deserialize(it)
                        // We just ignore broken entries
                        if (entry != null) {
                            addEntry(entry)
                        }
                    }
                }
                .build()
        }
    }
}
