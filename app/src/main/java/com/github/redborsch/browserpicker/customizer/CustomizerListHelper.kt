package com.github.redborsch.browserpicker.customizer

import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.recyclerview.setVerticalLinearLayoutManager

class CustomizerListHelper {

    fun setUp(
        list: RecyclerView,
    ) {
        list.setVerticalLinearLayoutManager()
    }
}