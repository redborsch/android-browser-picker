package com.github.redborsch.browserpicker.chooser

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.databinding.ActivityChooserBinding
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.forceReplaceCurrentFragment
import com.github.redborsch.fragment.showDialog
import com.github.redborsch.log.getLogger

class ChooserActivityPresenter(
    private val activity: FragmentActivity,
    binding: ActivityChooserBinding,
) {

    private val log = getLogger()

    private val fragmentManager get() = activity.supportFragmentManager
    private val fragmentTag get() = activity.defaultFragmentTag
    private val window get() = activity.window

    private val fragmentContainer = binding.fragmentContainer

    private var showMultiWindowUI: Boolean? = null
        set(value) {
            if (field != value) {
                log.d { "showMultiWindowUI: $field -> $value" }

                val oldValue = field
                field = value
                if (value != null) {
                    replaceFragment(value, oldValue == true)
                }
            }
        }

    fun updateUI() {
        showMultiWindowUI = activity.isInMultiWindowMode
    }

    private fun replaceFragment(multiWindow: Boolean, wasInMultiWindowMode: Boolean) {
        fragmentContainer.isVisible = multiWindow
        if (multiWindow) {
            window.disableFloating(true)
            showOccupyingWholeWindow()
        } else {
            if (wasInMultiWindowMode) {
                Handler(Looper.getMainLooper()).post {
                    window.disableFloating(false)
                    showBottomSheet()
                }
            } else {
                showBottomSheet()
            }
        }
    }

    private fun showBottomSheet() {
        fragmentManager.showDialog(fragmentTag, forceReplace = true) {
            BrowserChooserBottomSheetFragment()
        }
    }

    private fun showOccupyingWholeWindow() {
        fragmentManager.forceReplaceCurrentFragment(fragmentTag, fragmentContainer) {
            BrowserChooserFragment()
        }
    }

    private fun Window.disableFloating(value: Boolean) {
        val size = if (value) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT
        }
        setLayout(size, size)
    }
}
