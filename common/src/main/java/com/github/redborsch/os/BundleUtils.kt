package com.github.redborsch.os

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat

inline fun <reified T : Parcelable> Bundle.requireParcelable(key: String): T =
    requireNotNull(
        BundleCompat.getParcelable(
            this,
            key,
            T::class.java
        )
    ) {
        "Parcelable with key $key was null"
    }
