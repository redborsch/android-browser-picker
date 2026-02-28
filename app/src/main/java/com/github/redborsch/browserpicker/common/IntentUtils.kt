package com.github.redborsch.browserpicker.common

import android.content.Intent

fun Intent.makeCopyWithoutComponent() = Intent(this).apply {
    setComponent(null)
}
