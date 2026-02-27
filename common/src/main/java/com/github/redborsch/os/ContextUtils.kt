package com.github.redborsch.os

import android.content.Context
import androidx.core.content.ContextCompat

inline fun <reified T> Context.requireSystemService(): T =
    requireNotNull(
        ContextCompat.getSystemService(this, T::class.java)
    ) {
        "System service with class ${T::class.qualifiedName} is not available"
    }
