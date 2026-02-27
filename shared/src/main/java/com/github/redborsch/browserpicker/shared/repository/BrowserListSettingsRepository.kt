package com.github.redborsch.browserpicker.shared.repository

import android.net.Uri
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.model.BrowserListRepository
import com.github.redborsch.browserpicker.shared.repository.common.AbstractBrowserListRepository

class BrowserListSettingsRepository(
    private val wrappedRepository: AbstractBrowserListRepository,
    private val settings: BrowserListSettings,
    private val applyVisibility: Boolean,
) : BrowserListRepository {

    override suspend fun queryBrowserList(uri: Uri): List<BrowserData> =
        wrappedRepository.queryIntermediate(uri)
            .maybeApplyVisibility()
            .toList()
            .sortedBy {
                settings.getOrder(it)
            }

    private fun Sequence<BrowserData>.maybeApplyVisibility(): Sequence<BrowserData> =
        if (applyVisibility) {
            filter {
                settings.isVisible(it)
            }
        } else {
            this
        }
}
