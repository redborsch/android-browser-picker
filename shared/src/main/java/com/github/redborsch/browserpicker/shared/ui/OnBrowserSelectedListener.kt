package com.github.redborsch.browserpicker.shared.ui

import com.github.redborsch.browserpicker.shared.model.BrowserData

fun interface OnBrowserSelectedListener {

    fun onBrowserSelected(browserData: BrowserData)
}