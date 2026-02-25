package com.github.redborsch.browserpicker.customizer

import com.github.redborsch.browserpicker.shared.model.BrowserData

class CustomizerData(
    val browserData: BrowserData,
    var isVisible: Boolean,
) {
    fun toggleVisibility() {
        isVisible = !isVisible
    }
}