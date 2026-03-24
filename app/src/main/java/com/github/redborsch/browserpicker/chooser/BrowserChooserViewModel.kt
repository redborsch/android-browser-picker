package com.github.redborsch.browserpicker.chooser

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.data.BrowserListRepositoryFactory
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.log.getLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class BrowserChooserViewModel(application: Application) : AndroidViewModel(application) {

    private val log = getLogger()

    private val _uriActions = MutableStateFlow(emptyList<BrowserData>())
    val uriActions: Flow<List<BrowserData>> = _uriActions.asStateFlow()

    private val repo = BrowserListRepositoryFactory(getApplication())
    val handlers get() = repo.data.handlers

    val settings = Settings.getInstance(application)

    var customBrowserListSettings: BrowserListSettings? = null

    private val _browserIntentFactory = MutableStateFlow<BrowserIntentFactory?>(null)
    val browserIntentFactory: Flow<BrowserIntentFactory> get() = _browserIntentFactory.filterNotNull()

    fun setBrowserIntentFactory(browserIntentFactory: BrowserIntentFactory) {
        _browserIntentFactory.value = browserIntentFactory
        updateActions(browserIntentFactory.uri)
    }

    private fun updateActions(uri: Uri) {
        log.d { "updateActions uri = $uri, custom settings = $customBrowserListSettings" }
        val browserListSettings = customBrowserListSettings ?: settings.browserList
        viewModelScope.fetchBrowserList(uri, browserListSettings)
    }

    private fun CoroutineScope.fetchBrowserList(uri: Uri, settings: BrowserListSettings) = launch {
        _uriActions.value = repo.createListRepository(settings).queryBrowserList(uri)
    }
}
