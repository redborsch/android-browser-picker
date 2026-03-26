package com.github.redborsch.browserpicker.chooser

import android.os.Bundle

class BottomSheetChooserActivity : AbstractChooserActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            BrowserChooserBottomSheetFragment().show(supportFragmentManager, null)
        }
    }
}
