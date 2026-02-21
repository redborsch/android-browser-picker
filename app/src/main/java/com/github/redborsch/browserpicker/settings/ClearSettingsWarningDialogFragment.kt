package com.github.redborsch.browserpicker.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.Settings

class ClearSettingsWarningDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_reset_warning_title)
            .setMessage(R.string.settings_reset_warning_explanation)
            .setCancelable(true)
            .setNeutralButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                resetSettings()
            }
            .create()

    private fun resetSettings() {
        val context = context ?: return
        Settings.getInstance(context).clear()

        (activity as? SettingsFragment.Host)?.onSettingsReset()
    }
}