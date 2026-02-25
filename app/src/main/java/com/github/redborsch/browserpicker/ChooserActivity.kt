package com.github.redborsch.browserpicker

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.chooser.BrowserChooserBottomSheetFragment
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog
import com.github.redborsch.insets.enableEdgeToEdge

class ChooserActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun onStart() {
        super.onStart()

        supportFragmentManager.showDialog(defaultFragmentTag) {
            BrowserChooserBottomSheetFragment()
        }
    }
}
