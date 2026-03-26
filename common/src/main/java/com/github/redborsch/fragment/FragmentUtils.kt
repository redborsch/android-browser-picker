package com.github.redborsch.fragment

import android.app.Activity
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import kotlin.reflect.KClass

/**
 * Shortcut for the cases when a [Fragment] has only a single tag needed (which is usually
 * the case when properly structuring the app).
 */
val Fragment.defaultFragmentTag: String
    get() = this::class.qualifiedName + ".DefaultFragmentTag"

/**
 * Shortcut for the cases when an [Activity] has only a single tag needed (which is usually
 * the case when properly structuring the app).
 */
val Activity.defaultFragmentTag: String
    get() = this::class.qualifiedName + ".DefaultFragmentTag"

fun FragmentManager.replaceCurrentFragment(
    tag: String,
    fragmentClass: KClass<out Fragment>,
    container: View,
    force: Boolean = false,
) {
    if (!force) {
        val existing = findFragmentByTag(tag)
        if (fragmentClass.isInstance(existing)) {
            return
        }
    }
    commitNow(allowStateLoss = true) {
        replace(
            container.id,
            fragmentClass.java.getDeclaredConstructor().newInstance(),
            tag
        )
    }
}

/**
 * Replaces the current fragment with the new instance of itself. Is useful after resetting the
 * preference fragment's preferences.
 */
fun FragmentManager.resetCurrentFragment(tag: String, container: View) {
    val fragment = findFragmentByTag(tag) ?: return
    replaceCurrentFragment(tag, fragment::class, container, force = true)
}

/**
 * Shows the dialog provided by the supplied [factory] unless the dialog with the supplied [tag]
 * already exists. When [forceReplace] is true, then it actually removes the previous instance and
 * shows the new one.
 */
inline fun <F : DialogFragment> FragmentManager.showDialog(
    tag: String,
    forceReplace: Boolean = false,
    factory: () -> F,
) {
    val existing = findFragmentByTag(tag)
    if (forceReplace) {
        factory().show(
            beginTransaction()
                .apply {
                    if (existing != null) {
                        remove(existing)
                    }
                },
            tag,
        )
    } else {
        if (existing != null) {
            return
        }
        factory().show(this, tag)
    }
}
