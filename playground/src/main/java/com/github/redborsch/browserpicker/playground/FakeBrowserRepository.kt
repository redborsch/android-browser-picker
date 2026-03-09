package com.github.redborsch.browserpicker.playground

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.common.AbstractBrowserListRepository
import kotlinx.coroutines.Job

class FakeBrowserRepository(
    private val itemCount: Int,
) : AbstractBrowserListRepository() {

    override suspend fun queryIntermediate(uri: Uri): Sequence<BrowserData> = sequence {
        repeat(itemCount) {
            yield(newFakeBrowser(it))
        }
    }

    private fun newFakeBrowser(index: Int) = FakeBrowserData(
        "Browser #$index"
    )
}

private class FakeBrowserData(
    private val name: String
) : BrowserData {

    override val packageName: String
        get() = "com.github.redborsch.browserpicker"

    override val isNonBrowserApplication: Boolean
        get() = false

    override suspend fun getName(context: Context): CharSequence = name

    override suspend fun loadIcon(context: Context): Drawable? = null
}
