package com.github.redborsch.browserpicker.shared.repository

class BrowserListSettings(capacity: Int) {

    private val entries = ArrayList<SettingsEntry>(capacity)

    fun serialize(): Set<String> {
        return entries.mapTo(HashSet()) {
            it.serialize()
        }
    }

    companion object {
        fun deserialize(serialized: Set<String>): BrowserListSettings {
            return BrowserListSettings(serialized.size)
        }
    }
}
