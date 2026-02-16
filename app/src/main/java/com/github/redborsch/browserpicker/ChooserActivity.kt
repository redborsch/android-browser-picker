package com.github.redborsch.browserpicker

import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.chooser.BrowserChooserBottomSheetFragment

class ChooserActivity : FragmentActivity() {

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
