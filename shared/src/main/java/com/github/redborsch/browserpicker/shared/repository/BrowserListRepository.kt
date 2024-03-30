package com.github.redborsch.browserpicker.shared.repository

import android.net.Uri
import com.github.redborsch.browserpicker.shared.model.BrowserData

interface BrowserListRepository {

    suspend fun queryBrowserList(uri: Uri): List<BrowserData>
}