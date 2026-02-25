package com.github.redborsch.recyclerview

import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DragKeyListener(
    private val viewHolder: RecyclerView.ViewHolder,
    private val dragController: DragController,
) : View.OnKeyListener {

    init {
        viewHolder.itemView.setFocusable(true)
    }

    override fun onKey(
        v: View,
        keyCode: Int,
        event: KeyEvent,
    ): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return false
        }
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                onKeyboardDragToggle(viewHolder)
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN ->
                return onKeyboardMoved(keyCode)

            else -> return false
        }
    }

    private fun onKeyboardDragToggle(viewHolder: RecyclerView.ViewHolder) {
        val isCurrentlyDragged = dragController.isBeingDragged(viewHolder)
        dragController.cancelDrag()
        if (!isCurrentlyDragged) {
            dragController.setBeingDragged(viewHolder)
        }
    }

    private fun onKeyboardMoved(keyCode: Int): Boolean {
        val delta = when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> -1
            KeyEvent.KEYCODE_DPAD_DOWN -> +1
            else -> return false
        }
        return dragController.move(delta)
    }
}
