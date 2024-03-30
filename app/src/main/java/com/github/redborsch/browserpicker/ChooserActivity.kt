package com.github.redborsch.browserpicker

import androidx.appcompat.app.AppCompatActivity

class ChooserActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if (hasNoChooserFragment()) {
            BrowserChooserBottomSheetFragment().show(supportFragmentManager, chooserTag)
        }
    }

    private fun hasNoChooserFragment(): Boolean =
        supportFragmentManager.findFragmentByTag(chooserTag) == null

    companion object {
        private val chooserTag = BrowserChooserBottomSheetFragment::class.qualifiedName
    }
}
