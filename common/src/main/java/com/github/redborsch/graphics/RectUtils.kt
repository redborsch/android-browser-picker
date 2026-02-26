package com.github.redborsch.graphics

import android.graphics.Rect
import kotlin.math.max

val Rect.max: Int
    get() = max(width(), height())