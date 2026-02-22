package com.github.redborsch.browserpicker.shared.model

import android.net.Uri

interface BrowserListRepository {

    suspend fun queryBrowserList(uri: Uri): List<BrowserData>
}