package com.github.redborsch.browserpicker.shared.repository

import android.content.Intent
import android.os.Bundle
import com.github.redborsch.browserpicker.shared.model.BrowserData

interface BrowserListSettings {

    fun isVisible(packageName: String): Boolean
    fun isVisible(browserData: BrowserData): Boolean
    fun getOrder(packageName: String): Int
    fun getOrder(browserData: BrowserData): Int
    fun serialize(): Set<String>

    class Builder(capacity: Int) {

        private val entries = ArrayList<SettingsEntry>(capacity)

        fun addEntry(entry: SettingsEntry) {
            entries += entry
        }

        fun build(): BrowserListSettings = BrowserListSettingsImpl(
            entries.associateByTo(HashMap(entries.size)) {
                it.appPackage
            }
        )
    }

    companion object {

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

private fun BrowserListSettings.serializeForBundle() = serialize().toTypedArray()

private inline fun deserializeFromBundle(retrieveRaw: () -> Array<String>?): BrowserListSettings =
    BrowserListSettings.deserialize(
        retrieveRaw()?.toSet() ?: emptySet()
    )

fun Bundle.putBrowserListSettings(key: String, value: BrowserListSettings) {
    putStringArray(key, value.serializeForBundle())
}

fun Intent.putExtra(key: String, value: BrowserListSettings) {
    putExtra(key, value.serializeForBundle())
}

fun Bundle.getBrowserListSettings(key: String): BrowserListSettings = deserializeFromBundle {
    getStringArray(key)
}

fun Intent.getBrowserListSettingsExtra(key: String): BrowserListSettings = deserializeFromBundle {
    getStringArrayExtra(key)
}

private class BrowserListSettingsImpl(
    val entries: Map<String, SettingsEntry>,
    /**
     * We reserve 0 for potentially higher priority entries, for instance non-browser apps entry.
     * Also when new browsers are installed and not configured, they will appear on top, but
     * not on the very top.
     */
    private val defaultOrder: Int = 1,
) : BrowserListSettings {

    override fun isVisible(packageName: String): Boolean =
        entries[packageName]?.visible ?: true

    override fun isVisible(browserData: BrowserData): Boolean =
        isVisible(browserData.packageName)

    override fun getOrder(packageName: String): Int =
        entries[packageName]?.order ?: defaultOrder

    override fun getOrder(browserData: BrowserData): Int =
        getOrder(browserData.packageName)

    override fun serialize(): Set<String> {
        return entries.values.mapTo(HashSet()) {
            it.serialize()
        }
    }
}
