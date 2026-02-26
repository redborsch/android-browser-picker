package com.github.redborsch.window

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import com.google.android.material.internal.WindowUtils

inline val Context.currentWindowBounds: Rect
    @SuppressLint("RestrictedApi")
    get() = WindowUtils.getCurrentWindowBounds(this)
