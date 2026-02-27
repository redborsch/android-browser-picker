package com.github.redborsch.browserpicker.shared.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job

interface BrowserData {

    val packageName: String

    val isNonBrowserApplication: Boolean

    fun getName(context: Context): CharSequence

    fun loadIcon(context: Context, lifecycleOwner: LifecycleOwner, block: (Drawable?) -> Unit): Job?
}
