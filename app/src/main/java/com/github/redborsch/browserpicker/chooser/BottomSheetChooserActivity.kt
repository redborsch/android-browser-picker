package com.github.redborsch.browserpicker.chooser

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog

class BottomSheetChooserActivity : AbstractChooserActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // On some devices and in some situations showing the dialog fragment right away causes
        // artifacts.
        // For example, on Galaxy Tab A8 (Android 14) when sharing URL from Chrome.
        Handler(Looper.getMainLooper()).post {
            if (!isFinishing) {
                supportFragmentManager.showDialog(defaultFragmentTag) {
                    BrowserChooserBottomSheetFragment()
                }
            }
        }
    }
}
