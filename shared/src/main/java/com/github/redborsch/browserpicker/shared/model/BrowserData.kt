package com.github.redborsch.browserpicker.shared.model

import android.content.Context
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

interface BrowserData {

    val packageName: String

    val isNonBrowserApplication: Boolean

    suspend fun getName(context: Context): CharSequence

    suspend fun loadIcon(context: Context): Drawable?
}

@OptIn(ExperimentalCoroutinesApi::class)
fun BrowserData.getNameWithTimeout(
    context: Context,
    timeout: Duration = 100.milliseconds,
): CharSequence = runBlocking(Dispatchers.Default) {
    select {
        onTimeout(timeout) {
            packageName
        }
        async {
            getName(context)
        }.onAwait {
            it
        }
    }
}
