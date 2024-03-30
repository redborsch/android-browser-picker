package com.github.redborsch.browserpicker.playground

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.BrowserListRepository
import com.github.redborsch.browserpicker.shared.repository.InstalledBrowserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _installedBrowsers = MutableStateFlow(emptyList<BrowserData>())
    val installedBrowsers: Flow<List<BrowserData>> = _installedBrowsers.asStateFlow()

    private val _fetchTime = MutableStateFlow<Long?>(null)
    val fetchTime: StateFlow<Long?> = _fetchTime.asStateFlow()

    var repoType: BrowserListRepoType = BrowserListRepoType.Installed
        set(value) {
            if (field != value) {
                field = value
                updateBrowserList()
            }
        }

    init {
        updateBrowserList()
    }

    private fun updateBrowserList() {
        _fetchTime.value = measureTimeMillis {
            viewModelScope.fetchBrowserList()
        }
    }

    private fun CoroutineScope.fetchBrowserList() = launch {
        val repo = repoType.createInstance(getApplication())
        _installedBrowsers.value = repo.queryBrowserList(uri)
    }

    companion object {
        val uri = Uri.parse("https://www.mozilla.org/")
    }
}

enum class BrowserListRepoType {
    Fake {
        override fun createInstance(context: Context): BrowserListRepository =
            FakeBrowserRepository()
    },
    Installed {
        override fun createInstance(context: Context): BrowserListRepository =
            InstalledBrowserRepository(context)
    },
    InstalledExcludePackage {
        override fun createInstance(context: Context): BrowserListRepository =
            InstalledBrowserRepository(context, "com.github.redborsch.browserpicker")
    };

    abstract fun createInstance(context: Context): BrowserListRepository
}