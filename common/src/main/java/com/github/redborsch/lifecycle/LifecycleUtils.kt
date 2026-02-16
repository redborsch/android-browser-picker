package com.github.redborsch.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

fun LifecycleOwner.launchOnEachStart(block: suspend () -> Unit) = lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        block()
    }
}