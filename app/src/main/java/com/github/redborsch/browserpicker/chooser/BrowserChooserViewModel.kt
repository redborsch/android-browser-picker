package com.github.redborsch.browserpicker.chooser

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.data.BrowserPickerRepository
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.log.getLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrowserChooserViewModel(application: Application) : AndroidViewModel(application) {

    private val log = getLogger()

    private val _uriActions = MutableStateFlow(emptyList<BrowserData>())
    val uriActions: Flow<List<BrowserData>> = _uriActions.asStateFlow()

    private val repo = BrowserPickerRepository(getApplication())
    val handlers get() = repo.handlers

    val settings = Settings.getInstance(application)

    var browserListSettings: BrowserListSettings = settings.browserList
        set(value) {
            field = value
            updateActions()
        }

    var uri: Uri? = null
        set(value) {
            field = value
            updateActions()
        }

    private fun updateActions() {
        val uri = uri ?: return
        log.d { "updateActions uri = $uri, settings = $browserListSettings" }
        viewModelScope.fetchBrowserList(uri, browserListSettings)
    }

    private fun CoroutineScope.fetchBrowserList(uri: Uri, settings: BrowserListSettings) = launch {
        _uriActions.value = repo.createListRepository(settings).queryBrowserList(uri)
    }
}
