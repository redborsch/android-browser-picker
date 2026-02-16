package com.github.redborsch.binding

import android.app.Activity
import androidx.viewbinding.ViewBinding

fun Activity.setContentView(binding: ViewBinding) {
    setContentView(binding.root)
}
