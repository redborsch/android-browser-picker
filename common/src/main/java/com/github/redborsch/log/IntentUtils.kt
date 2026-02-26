package com.github.redborsch.log

import android.content.Intent

fun Intent.dumpForLog(): String {
    return toUri(Intent.URI_INTENT_SCHEME).toString()
}
