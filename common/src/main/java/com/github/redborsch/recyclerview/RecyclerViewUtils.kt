package com.github.redborsch.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setVerticalLinearLayoutManager(){
    layoutManager = LinearLayoutManager(
        context,
        RecyclerView.VERTICAL,
        false,
    )
}
