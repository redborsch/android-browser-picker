package com.github.redborsch.widget

import android.content.Context
import android.text.method.MovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.method.LinkMovementMethodCompat

class LinkTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getDefaultMovementMethod(): MovementMethod =
        LinkMovementMethodCompat.getInstance()
}