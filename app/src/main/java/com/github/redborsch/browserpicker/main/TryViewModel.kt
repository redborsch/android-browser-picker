package com.github.redborsch.browserpicker.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class TryViewModel(
    private val isDefaultBrowser: Flow<Boolean?>,
) : ViewModel() {

    private val isPickerTried = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val shouldShowDefaultBrowserHint: Flow<Boolean> = isDefaultBrowser.flatMapLatest { isDefault ->
        if (isDefault == false) {
            isPickerTried
        } else {
            flowOf(false)
        }
    }

    init {
        viewModelScope.launch {
            isDefaultBrowser.dropWhile { it != false }.collect {
                isPickerTried.value = false
            }
        }
    }

    fun registerTryAttempt() {
        isPickerTried.value = true
    }

    companion object {
        fun createFactory(isDefaultBrowser: Flow<Boolean?>) = viewModelFactory {
            initializer<TryViewModel> {
                TryViewModel(isDefaultBrowser)
            }
        }
    }
}