package com.github.redborsch.browserpicker.customizer

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.data.BrowserListRepositoryFactory
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomizerViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = Settings.getInstance(application)

    private val _items = MutableStateFlow(emptyList<CustomizerData>())
    val items: Flow<List<CustomizerData>> = _items.asStateFlow()

    init {
        viewModelScope.fetchBrowserList(settings.testUrl.toUri())
    }

    fun saveSettings(browserListSettings: BrowserListSettings) {
        settings.browserList = browserListSettings
    }

    private fun CoroutineScope.fetchBrowserList(uri: Uri) = launch {
        val browserListSettings = settings.browserList
        val repo = BrowserListRepositoryFactory(
            getApplication(),
        ).createListRepositoryForCustomizing(settings.browserList)
        _items.value = repo.queryBrowserList(uri).map {
            CustomizerData(it, browserListSettings.isVisible(it.packageName))
        }
    }
}
