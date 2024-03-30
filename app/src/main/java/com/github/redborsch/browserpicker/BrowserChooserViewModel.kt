package com.github.redborsch.browserpicker

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.InstalledBrowserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrowserChooserViewModel(application: Application) : AndroidViewModel(application) {

    private val _installedBrowsers = MutableStateFlow(emptyList<BrowserData>())
    val installedBrowsers: Flow<List<BrowserData>> = _installedBrowsers.asStateFlow()

    fun updateBrowserList(uri: Uri) {
        viewModelScope.fetchBrowserList(uri)
    }

    private fun CoroutineScope.fetchBrowserList(uri: Uri) = launch {
        val context: Context = getApplication()
        val repo = InstalledBrowserRepository(context, context.packageName)
        _installedBrowsers.value = repo.queryBrowserList(uri)
    }
}