package com.github.redborsch.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.github.redborsch.binding.ViewBindingInflate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.internal.EdgeToEdgeUtils

abstract class BottomSheetDialogFragment<VB : ViewBinding>(
    private val factory: ViewBindingInflate<VB>,
) : BottomSheetDialogFragment() {

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = factory(layoutInflater, null, false)
        return BottomSheetDialog(requireContext(), theme).apply {
            setContentView(binding.root)
            applyEdgeToEdgePatches(window!!)
            binding.setUp(this)
        }
    }

    protected abstract fun VB.setUp(lifecycleOwner: LifecycleOwner)

    /**
     * Edge-to-edge logic was borrowed from
     * https://github.com/material-components/material-components-android
     */
    @SuppressLint("RestrictedApi", "WrongConstant")
    private fun applyEdgeToEdgePatches(window: Window) {
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            val leftInset: Int
            val rightInset: Int
            if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
                leftInset = insets.getInsets(WindowInsets.Type.systemBars()).left
                rightInset = insets.getInsets(WindowInsets.Type.systemBars()).right
            } else {
                leftInset = insets.stableInsetLeft
                rightInset = insets.stableInsetRight
            }

            v.setPadding(leftInset, 0, rightInset, 0)
            insets
        }
    }
}