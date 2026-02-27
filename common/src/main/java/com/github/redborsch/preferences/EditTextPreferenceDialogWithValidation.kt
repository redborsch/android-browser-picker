package com.github.redborsch.preferences

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.preference.EditTextPreferenceDialogFragmentCompat
import com.github.redborsch.R
import com.github.redborsch.log.getLogger
import com.github.redborsch.os.requireParcelable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditTextPreferenceDialogWithValidation : EditTextPreferenceDialogFragmentCompat() {

    private val log = getLogger()

    private lateinit var validator: ValidationStrategy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validator = requireArguments().requireParcelable(ARG_VALIDATION_STRATEGY)
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        val inputLayout = view.findViewById<TextInputLayout>(R.id.input_layout) ?: run {
            log.e {
                "Cannot find the required view. Make sure you supply ${
                    resources.getResourceName(R.layout.preference_dialog_edittext_with_errors)
                } as preference dialogLayout property"
            }
            return
        }
        val editText = view.findViewById<TextInputEditText>(android.R.id.edit)
        editText.doAfterTextChanged {
            val errorTextResId = validator.validate(it ?: "")
            inputLayout.error = if (errorTextResId != null) {
                getString(errorTextResId)
            } else {
                null
            }
            enablePositiveButton(inputLayout.error == null)
        }
    }

    private fun enablePositiveButton(enable: Boolean) {
        val alertDialog = dialog as? AlertDialog ?: run {
            log.e { "Dialog instance was $dialog instead of ${AlertDialog::class}" }
            return
        }
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE) ?: run {
            log.e { "Dialog has no positive button" }
            return
        }
        positiveButton.isEnabled = enable
    }

    companion object {

        private const val ARG_VALIDATION_STRATEGY = "validationStrategy"

        fun newInstance(key: String, validationStrategy: ValidationStrategy) =
            EditTextPreferenceDialogWithValidation().apply {
                arguments = Bundle(2).apply {
                    putString(ARG_KEY, key)
                    putParcelable(ARG_VALIDATION_STRATEGY, validationStrategy)
                }
            }
    }
}

interface ValidationStrategy : Parcelable {
    @StringRes
    fun validate(text: CharSequence): Int?
}
