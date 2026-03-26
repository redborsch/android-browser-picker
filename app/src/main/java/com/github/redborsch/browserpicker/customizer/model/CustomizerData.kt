package com.github.redborsch.browserpicker.customizer.model

import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.browserpicker.shared.repository.SettingsEntry
import com.github.redborsch.recyclerview.Rearrangeable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface CustomizerData : Rearrangeable {

    operator fun get(index: Int): CustomizerItem

    fun toBrowserListSettings(): BrowserListSettings
}

suspend fun CustomizerData(
    browserList: List<BrowserData>,
    browserListSettings: BrowserListSettings,
): CustomizerData = withContext(Dispatchers.Default) {
    val items = browserList.mapTo(ArrayList<CustomizerItem>(browserList.size)) {
        CustomizerItemImpl(it, browserListSettings.isVisible(it.packageName))
    }
    CustomizerDataImpl(items)
}

interface CustomizerItem {
    val browserData: BrowserData
    val isVisible: Boolean

    fun toggleVisibility()
}

private class CustomizerDataImpl(
    private val items: MutableList<CustomizerItem>,
) : CustomizerData {

    override val size: Int get() = items.size

    override fun get(index: Int): CustomizerItem = items[index]

    override fun exchange(fromPosition: Int, toPosition: Int): Boolean =
        items.exchange(fromPosition, toPosition)

    override fun toBrowserListSettings(): BrowserListSettings {
        val builder = BrowserListSettings.Builder(items.size)
        items.forEachIndexed { index, data ->
            builder.addEntry(
                SettingsEntry(
                    data.browserData.packageName,
                    data.isVisible,
                    index,
                )
            )
        }
        return builder.build()
    }
}

private class CustomizerItemImpl(
    override val browserData: BrowserData,
    isVisible: Boolean,
) : CustomizerItem {

    override var isVisible: Boolean = isVisible
        private set

    override fun toggleVisibility() {
        isVisible = !isVisible
    }
}

private fun <E> MutableList<E>.exchange(fromPosition: Int, toPosition: Int): Boolean {
    if (fromPosition == toPosition) {
        return false
    }
    if (isValidIndex(fromPosition) && isValidIndex(toPosition)) {
        val from = get(fromPosition)
        set(fromPosition, get(toPosition))
        set(toPosition, from)
        return true
    }
    return false
}

private fun List<*>.isValidIndex(index: Int): Boolean = index in 0..<size
