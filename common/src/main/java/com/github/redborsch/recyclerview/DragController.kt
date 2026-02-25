package com.github.redborsch.recyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

interface DragController {

    fun startDrag(viewHolder: RecyclerView.ViewHolder)
    fun cancelDrag()

    fun isBeingDragged(viewHolder: RecyclerView.ViewHolder): Boolean
    fun setBeingDragged(viewHolder: RecyclerView.ViewHolder)

    fun move(positionDelta: Int): Boolean
}

interface DraggableViewHolder {
    fun setIsDragged(dragged: Boolean)
}

internal fun interface Swapper {
    fun swap(fromPosition: Int, toPosition: Int): Boolean
}

internal class DragControllerImpl(
    private val swapper: Swapper,
) : DragController {

    private var draggedViewHolder: RecyclerView.ViewHolder? = null
        set(value) {
            (field as? DraggableViewHolder)?.setIsDragged(false)
            field = value
            (value as? DraggableViewHolder)?.setIsDragged(true)
        }

    private val itemTouchHelper = ItemTouchHelper(
        ItemTouchHelperCallback(this, swapper),
    )

    fun attach(recyclerView: RecyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun startDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun cancelDrag() {
        draggedViewHolder = null
    }

    override fun isBeingDragged(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder === draggedViewHolder
    }

    override fun setBeingDragged(viewHolder: RecyclerView.ViewHolder) {
        draggedViewHolder = viewHolder
        (viewHolder.itemView as MaterialCardView).isDragged = true
    }

    override fun move(positionDelta: Int): Boolean {
        val draggedViewHolder = draggedViewHolder ?: return false

        val fromPosition = draggedViewHolder.getBindingAdapterPosition()
        if (fromPosition == RecyclerView.NO_POSITION) {
            return false
        }
        return swapper.swap(fromPosition, fromPosition + positionDelta)
    }
}

private class ItemTouchHelperCallback(
    private val dragController: DragController,
    private val swapper: Swapper,
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0,
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.getBindingAdapterPosition()
        val toPosition = target.getBindingAdapterPosition()
        swapper.swap(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int
    ) {
        // Do nothing for now
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
            dragController.cancelDrag()
            dragController.setBeingDragged(viewHolder)
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            dragController.cancelDrag()
        }
    }
}
