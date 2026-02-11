package com.github.redborsch.browserpicker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.databinding.ActivityMainBinding
import com.github.redborsch.browserpicker.shared.fragment.setContentView

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding)

        binding.setUp()
    }

    fun ActivityMainBinding.setUp() {
        tryItOut.setOnClickListener {
            openChooser("https://redborsch.github.io/android-browser-picker/".toUri())
        }

        privacyNotes.movementMethod = LinkMovementMethodCompat.getInstance()
    }

    private fun openChooser(url: Uri) {
        Intent(this, ChooserActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = url
        }.let {
            startActivity(it)
        }
    }
}
