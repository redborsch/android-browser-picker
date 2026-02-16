package com.github.redborsch.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import kotlin.reflect.KClass

fun FragmentManager.replaceCurrentFragment(fragmentClass: KClass<out Fragment>, container: View) {
    val tag = fragmentClass.qualifiedName
    if (findFragmentByTag(tag) != null) {
        return
    }
    commitNow(allowStateLoss = true) {
        replace(
            container.id,
            fragmentClass.java.getDeclaredConstructor().newInstance(),
            tag
        )
    }
}
