package com.github.redborsch.browserpicker.setup

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.redborsch.browserpicker.R

class OpenSettingsErrorDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.setup_error_title)
            .setMessage(R.string.setup_error_explanation)
            .setCancelable(true)
            .setNeutralButton(android.R.string.cancel, null)
            .create()
}