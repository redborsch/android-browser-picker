package com.github.redborsch.browserpicker.shared.repository.common

import android.net.Uri
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.model.BrowserListRepository

abstract class AbstractBrowserListRepository : BrowserListRepository {

    abstract suspend fun queryIntermediate(uri: Uri): Sequence<BrowserData>

    final override suspend fun queryBrowserList(uri: Uri): List<BrowserData> =
        queryIntermediate(uri).toList()
}
