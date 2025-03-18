package com.github.redborsch.browserpicker.playground

import android.app.Application
import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.repository.BrowserListRepository
import com.github.redborsch.browserpicker.shared.repository.InstalledBrowserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _repoData = MutableStateFlow(RepoData())
    val repoData: StateFlow<RepoData> = _repoData.asStateFlow()

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
        measureTimeMillis {
            viewModelScope.fetchBrowserList()
        }
    }

    private fun CoroutineScope.fetchBrowserList() = launch {
        val repo = repoType.createInstance(getApplication())
        val browsers: List<BrowserData>
        val fetchTime = measureTimeMillis {
            browsers = repo.queryBrowserList(uri)
        }
        _repoData.value = RepoData(fetchTime, browsers)
    }

    companion object {
        val uri = "https://play.google.com/store/apps/details?id=com.github.redborsch.browserpicker".toUri()
    }
}

class RepoData(
    val fetchTime: Long? = null,
    val browsers: List<BrowserData> = emptyList(),
)

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