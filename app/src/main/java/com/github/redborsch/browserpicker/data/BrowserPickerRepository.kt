package com.github.redborsch.browserpicker.data

import android.content.Context
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.model.BrowserListRepository
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettingsRepository
import com.github.redborsch.browserpicker.shared.repository.CustomActionsRepository
import com.github.redborsch.browserpicker.shared.repository.installed.InstalledBrowserFilter
import com.github.redborsch.browserpicker.shared.repository.installed.InstalledBrowserRepository

class BrowserPickerRepository(
    private val context: Context,
) {

    private val nonBrowserAppEntry = InternalBrowserData(
        Globals.internalAction("non-browser-apps"),
        R.string.internal_entry_non_browser_apps,
        R.drawable.outline_question_mark_24
    )

    private val ownPackageFilter = InstalledBrowserRepository.filterOutPackage(context.packageName)

    val handlers = buildList(3) {
        add(CopyActionHandler())
        add(ShareActionHandler())
        // Must always be the last one
        add(InstalledBrowserHandler())
    }

    private val handlersActions = handlers
        .asSequence()
        .mapNotNull {
            it.browserData
        }

    fun createListRepository(browserListSettings: BrowserListSettings): BrowserListRepository =
        createBrowserListRepository(
            browserListSettings = BrowserListSettingsWrapper(
                browserListSettings,
                nonBrowserAppEntry,
            ),
            applyVisibility = true,
            filters = listOf(ownPackageFilter),
            actions = handlersActions,
        )

    fun createListRepositoryForCustomizing(browserListSettings: BrowserListSettings): BrowserListRepository =
        createBrowserListRepository(
            browserListSettings = browserListSettings,
            applyVisibility = false,
            filters = listOf(
                ownPackageFilter,
                // There's no good reason to store settings for apps that are pertinent to specific links
                InstalledBrowserRepository.filterOutNonBrowserApps(),
            ),
            actions = handlersActions + nonBrowserAppEntry,
        )

    private fun createBrowserListRepository(
        browserListSettings: BrowserListSettings,
        applyVisibility: Boolean = true,
        filters: List<InstalledBrowserFilter>,
        actions: Sequence<BrowserData>,
    ): BrowserListRepository = BrowserListSettingsRepository(
        CustomActionsRepository(
            InstalledBrowserRepository(context, filters),
            actions.toList(),
        ),
        browserListSettings,
        applyVisibility,
    )
}

private class BrowserListSettingsWrapper(
    private val original: BrowserListSettings,
    private val nonBrowserAppEntry: BrowserData,
) : BrowserListSettings by original {

    override fun getOrder(browserData: BrowserData): Int {
        if (browserData.isNonBrowserApplication) {
            return getOrder(nonBrowserAppEntry.packageName)
        }
        return original.getOrder(browserData)
    }

    override fun isVisible(browserData: BrowserData): Boolean {
        if (browserData.isNonBrowserApplication) {
            return isVisible(nonBrowserAppEntry.packageName)
        }
        return original.isVisible(browserData)
    }
}
