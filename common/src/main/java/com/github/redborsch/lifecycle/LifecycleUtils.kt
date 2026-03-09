package com.github.redborsch.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LifecycleOwner.launchOnEachStart(block: suspend CoroutineScope.() -> Unit) =
    launchOnEach(Lifecycle.State.STARTED, block)

inline fun LifecycleOwner.launchOnEach(
    state: Lifecycle.State,
    crossinline block: suspend CoroutineScope.() -> Unit,
) = lifecycleScope.launch {
    repeatOnLifecycle(state) {
        block()
    }
}
