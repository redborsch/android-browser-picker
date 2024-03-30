package com.github.redborsch.browserpicker.playground

import android.graphics.drawable.Drawable
import android.net.Uri
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.BrowserListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeBrowserRepository : BrowserListRepository {

    override suspend fun queryBrowserList(uri: Uri): List<BrowserData> = buildList {
        repeat(10) {
            add(newFakeBrowser(it))
        }
    }

    private fun newFakeBrowser(index: Int) = FakeBrowserData(
        "Browser #$index"
    )
}

private class FakeBrowserData(
    override val name: String
) : BrowserData {
    override val icon: Flow<Drawable?> = flowOf(null)
    override val packageName: String
        get() = "com.github.redborsch.browserpicker"
}
