package com.github.redborsch.browserpicker.shared.repository

import android.net.Uri
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.common.AbstractBrowserListRepository

class CustomActionsRepository(
    private val wrappedRepository: AbstractBrowserListRepository,
    private val customActions: List<BrowserData>,
) : AbstractBrowserListRepository() {

    override suspend fun queryIntermediate(uri: Uri): Sequence<BrowserData> {
        return wrappedRepository.queryIntermediate(uri) + customActions
    }
}
