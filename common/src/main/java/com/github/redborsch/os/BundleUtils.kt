package com.github.redborsch.os

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.IntentCompat
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

inline fun <reified T : Parcelable> Intent.tryGetParcelableExtra(key: String): T? =
    IntentCompat.getParcelableExtra(this, key, T::class.java)
