package com.github.redborsch.browserpicker

import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.chooser.BrowserChooserBottomSheetFragment
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog

class ChooserActivity : FragmentActivity() {

    override fun onStart() {
        super.onStart()

        supportFragmentManager.showDialog(defaultFragmentTag) {
            BrowserChooserBottomSheetFragment()
        }
    }
}
