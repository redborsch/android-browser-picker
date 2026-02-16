package com.github.redborsch.browserpicker.shared.system

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

val Context.roleManager: RoleManager
    @RequiresApi(Build.VERSION_CODES.Q)
    get() = getSystemService(RoleManager::class.java)

val RoleManager.isDefaultBrowser: Boolean
    @RequiresApi(Build.VERSION_CODES.Q)
    get() = isRoleAvailable(RoleManager.ROLE_BROWSER) &&
            isRoleHeld(RoleManager.ROLE_BROWSER)

@RequiresApi(Build.VERSION_CODES.Q)
fun RoleManager.createBrowserRoleIntent(): Intent =
    createRequestRoleIntent(RoleManager.ROLE_BROWSER)
