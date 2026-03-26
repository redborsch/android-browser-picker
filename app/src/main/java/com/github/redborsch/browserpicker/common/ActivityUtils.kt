package com.github.redborsch.browserpicker.common

import android.app.ActivityManager
import android.content.Context
import com.github.redborsch.os.requireSystemService

val Context.activityManager: ActivityManager get() = requireSystemService()