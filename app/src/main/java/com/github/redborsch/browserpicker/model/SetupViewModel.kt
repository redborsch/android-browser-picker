package com.github.redborsch.browserpicker.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.github.redborsch.browserpicker.shared.system.isDefaultBrowser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

class SetupViewModel(application: Application) : AndroidViewModel(application) {

    private val refreshSignal = Channel<Boolean>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val isDefault: Flow<Boolean?> = flow {
        emit(null)
        do {
            emit(isDefaultBrowser(getApplication()))
        } while (refreshSignal.receive())
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun createRefresher() = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START,
            Lifecycle.Event.ON_RESUME ->
                refreshDefaultState()

            else -> {}
        }
    }

    fun refreshDefaultState() {
        refreshSignal.trySend(true)
    }
}
