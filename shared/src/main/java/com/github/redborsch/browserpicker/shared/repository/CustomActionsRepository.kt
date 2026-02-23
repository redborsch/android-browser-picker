package com.github.redborsch.browserpicker.shared.repository

import android.net.Uri
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.model.BrowserListRepository

class CustomActionsRepository(
    private val wrappedRepository: BrowserListRepository,
) : BrowserListRepository {

    override suspend fun queryBrowserList(uri: Uri): List<BrowserData> {
        return wrappedRepository.queryBrowserList(uri)
    }
}
