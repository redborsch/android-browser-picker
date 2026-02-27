package com.github.redborsch.browserpicker.data

import android.content.Context
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.model.BrowserListRepository
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettingsRepository
import com.github.redborsch.browserpicker.shared.repository.CustomActionsRepository
import com.github.redborsch.browserpicker.shared.repository.installed.InstalledBrowserFilter
import com.github.redborsch.browserpicker.shared.repository.installed.InstalledBrowserRepository

class BrowserListRepositoryFactory(
    private val context: Context,
    val data: BrowserPickerRepository = BrowserPickerRepository(),
) {

    private val ownPackageFilter = InstalledBrowserRepository.filterOutPackage(
        Globals.ownPackageName
    )

    fun createListRepository(browserListSettings: BrowserListSettings): BrowserListRepository =
        createBrowserListRepository(
            browserListSettings = BrowserListSettingsWrapper(
                browserListSettings,
                data.nonBrowserAppEntry,
            ),
            applyVisibility = true,
            filters = listOf(ownPackageFilter),
            actions = data.handlersActions,
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
            actions = data.handlersActions + data.nonBrowserAppEntry,
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
