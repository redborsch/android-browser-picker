package com.github.redborsch.browserpicker.customizer

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.installed.InstalledBrowserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomizerViewModel(application: Application) : AndroidViewModel(application) {

    private val _installedBrowsers = MutableStateFlow(emptyList<CustomizerData>())
    val installedBrowsers: Flow<List<CustomizerData>> = _installedBrowsers.asStateFlow()

    init {
        val context: Context = getApplication()
        updateBrowserList(Settings.getInstance(context).testUrl.toUri())
    }

    fun updateBrowserList(uri: Uri) {
        viewModelScope.fetchBrowserList(uri)
    }

    private fun CoroutineScope.fetchBrowserList(uri: Uri) = launch {
        val context: Context = getApplication()
        val repo = InstalledBrowserRepository(context, context.packageName)
        _installedBrowsers.value = repo.queryBrowserList(uri).map {
            // FIXME
            CustomizerData(it, true)
        }
    }
}