package com.github.redborsch.log

import android.content.Intent

fun Intent.dumpForLog(): String = buildString {
    appendLine("Action: $action")
    appendLine("Package: ${getPackage()}")
    appendLine("uri = ${toUri(Intent.URI_INTENT_SCHEME)}")
}
