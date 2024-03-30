package com.github.redborsch.browserpicker.shared.model

import android.graphics.drawable.Drawable
import kotlinx.coroutines.flow.Flow

interface BrowserData {

    val name: String

    val icon: Flow<Drawable?>

    val packageName: String
}
