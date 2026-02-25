package com.github.redborsch.recyclerview

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DragHandlerTouchListener(
    private val viewHolder: RecyclerView.ViewHolder,
    private val dragController: DragController,
) : View.OnTouchListener {

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dragController.startDrag(viewHolder)
                true
            }

            MotionEvent.ACTION_UP -> v.performClick()
            else -> false
        }
    }
}
