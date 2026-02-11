package com.github.redborsch.browserpicker.system

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

class SystemCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()

    private val refreshSignal = Channel<Boolean>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val isDefault: Flow<Boolean?> = flow {
        emit(null)
        do {
            emit(isDefaultBrowser())
        } while (refreshSignal.receive())
    }.flowOn(Dispatchers.Default).shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun refreshDefaultState() {
        refreshSignal.trySend(true)
    }

    private fun queryDefaultBrowserPackageName(): String {
        val intent = Intent(Intent.ACTION_VIEW, "http://".toUri())
        val resolveInfo = context.packageManager.resolveActivity(intent,
            PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.run { activityInfo.packageName } ?: ""
    }

    private fun isDefaultBrowser(): Boolean =
        queryDefaultBrowserPackageName() == context.packageName
}
