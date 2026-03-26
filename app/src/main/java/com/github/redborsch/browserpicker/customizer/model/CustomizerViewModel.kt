package com.github.redborsch.browserpicker.customizer.model

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.data.BrowserListRepositoryFactory
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.browserpicker.shared.repository.getBrowserListSettings
import com.github.redborsch.browserpicker.shared.repository.putBrowserListSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class CustomizerViewModel(
    application: Application,
    state: SavedStateHandle,
) : AndroidViewModel(application) {

    private val settings = Settings.getInstance(application)

    private val _data = MutableStateFlow<CustomizerData?>(null)
    val data: Flow<CustomizerData> get() = _data.filterNotNull()
    val currentData: CustomizerData? get() = _data.value

    init {
        viewModelScope.fetchBrowserList(
            settings.testUrl.toUri(),
            state.get<Bundle>(KEY_CUSTOMIZER_ITEMS)?.retrieveBrowserListSettings()
                ?: settings.browserList,
        )

        state.setSavedStateProvider(KEY_CUSTOMIZER_ITEMS) {
            _data.value.toBundle()

        }
    }

    fun saveSettings() {
        _data.value?.let {
            settings.browserList = it.toBrowserListSettings()
        }
    }

    private fun CoroutineScope.fetchBrowserList(uri: Uri, browserListSettings: BrowserListSettings) = launch {
        val repo = BrowserListRepositoryFactory(
            getApplication(),
        ).createListRepositoryForCustomizing(browserListSettings)
        _data.value = CustomizerData(
            repo.queryBrowserList(uri),
            browserListSettings,
        )
    }

    companion object {
        private const val KEY_CUSTOMIZER_ITEMS = "customizer_items"
        private const val KEY_BROWSER_LIST_SETTINGS = "browser_list_settings"

        private fun CustomizerData?.toBundle(): Bundle = if (this != null) {
            Bundle(1).apply {
                putBrowserListSettings(KEY_BROWSER_LIST_SETTINGS, toBrowserListSettings())
            }
        } else {
            Bundle.EMPTY
        }

        private fun Bundle.retrieveBrowserListSettings(): BrowserListSettings =
            getBrowserListSettings(KEY_BROWSER_LIST_SETTINGS)
    }
}
