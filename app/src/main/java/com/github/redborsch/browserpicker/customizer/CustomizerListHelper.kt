package com.github.redborsch.browserpicker.customizer

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.R
import com.github.redborsch.lifecycle.launchOnEachStart
import com.github.redborsch.log.Logger
import com.github.redborsch.log.getLogger
import com.github.redborsch.recyclerview.setVerticalLinearLayoutManager
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Runnable

class CustomizerListHelper(
    private val viewModel: CustomizerViewModel,
) {

    fun setUp(
        list: RecyclerView,
        lifecycleOwner: LifecycleOwner,
    ) {
        val adapter = CustomizerAdapter(lifecycleOwner)
        list.setVerticalLinearLayoutManager()
        list.adapter = adapter

        lifecycleOwner.updateBrowserList(adapter)

        val callback: ItemTouchHelper.Callback = CardItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(list)
    }

    private fun LifecycleOwner.updateBrowserList(
        adapter: CustomizerAdapter,
    ) = launchOnEachStart {
        viewModel.installedBrowsers.collect {
            adapter.updateList(it)
        }
    }
/*
    private fun RecyclerView.fixHidingAppBarWhenNotScrolling(
        lifecycleOwner: LifecycleOwner,
        adapter: RecyclerView.Adapter<*>,
    ) {
        log.d { "Installing fixHidingAppBarWhenNotScrolling patch" }
        val list = this
        // Disable until we know for sure we'd need it.
        list.isNestedScrollingEnabled = false

        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    log.d { "Adapter.onChanged, itemCount = ${adapter.itemCount}" }
                    if (adapter.itemCount > 0) {
                        NestedScrollEnableCallback(list, lifecycleOwner, log)

                        adapter.unregisterAdapterDataObserver(this)
                    }
                }
            }
        )
    }
 */
}
/*
private class NestedScrollEnableCallback(
    private val list: RecyclerView,
    private val lifecycleOwner: LifecycleOwner,
    private val log: Logger,
) : Runnable, LifecycleEventObserver {

    init {
        list.post(this)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun run() {
        val range = list.computeVerticalScrollRange()
        // Should in fact exclude AppBar height as it will disable scrolling also in case
        // RecyclerView has just enough space to fit the items. However, a part of the RecyclerView
        // is being hidden from the view, and it should be possible to drag it up.
        val height = list.height
        list.isNestedScrollingEnabled = range > height
        log.d { "RecyclerView.post, range: $range, height $height -> " +
                "isNestedScrollingEnabled = ${list.isNestedScrollingEnabled}" }
        if (range == 0) {
            list.post(this)
        } else {
            lifecycleOwner.lifecycle.removeObserver(this)
            log.d { "Lifecycle observer removed, all done" }
        }
    }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        log.d { "Lifecycle.onStateChanged $event" }
        when (event) {
            Lifecycle.Event.ON_STOP -> list.removeCallbacks(this)
            Lifecycle.Event.ON_DESTROY -> lifecycleOwner.lifecycle.removeObserver(this)
            else -> {}
        }
    }
}
*/
private class CardAdapter private constructor(private val cardNumbers: IntArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>(), OnKeyboardDragListener {
    private var draggedViewHolder: RecyclerView.ViewHolder? = null

    private var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.getContext())
        val view: View =
            inflater.inflate(R.layout.item_customizer_entry, parent,  /* attachToRoot= */false)
        return CardViewHolder(view, this)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as CardViewHolder).bind(cardNumbers[position])
    }

    override fun getItemCount(): Int {
        return cardNumbers.size
    }

    fun setItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    fun swapCards(fromPosition: Int, toPosition: Int) {
        if (fromPosition < 0 || fromPosition >= cardNumbers.size || toPosition < 0 || toPosition >= cardNumbers.size) {
            return
        }

        val fromNumber = cardNumbers[fromPosition]
        cardNumbers[fromPosition] = cardNumbers[toPosition]
        cardNumbers[toPosition] = fromNumber
        notifyItemMoved(fromPosition, toPosition)
    }

    fun cancelDrag() {
        if (draggedViewHolder != null) {
            (draggedViewHolder!!.itemView as MaterialCardView).setDragged(false)
            draggedViewHolder = null
        }
    }

    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper!!.startDrag(viewHolder)
    }

    override fun onKeyboardDragToggle(viewHolder: RecyclerView.ViewHolder) {
        val isCurrentlyDragged = draggedViewHolder === viewHolder
        cancelDrag()
        if (!isCurrentlyDragged) {
            draggedViewHolder = viewHolder
            (viewHolder.itemView as MaterialCardView).setDragged(true)
        }
    }

    override fun onKeyboardMoved(keyCode: Int): Boolean {
        if (draggedViewHolder == null) {
            return false
        }

        val fromPosition = draggedViewHolder!!.getBindingAdapterPosition()
        if (fromPosition == RecyclerView.NO_POSITION) {
            return false
        }

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                swapCards(fromPosition, fromPosition - 1)
                return true
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                swapCards(fromPosition, fromPosition + 1)
                return true
            }

            else -> return false
        }
    }

    private class CardViewHolder(itemView: View, listener: OnKeyboardDragListener) :
        RecyclerView.ViewHolder(itemView) {
//        private val titleView: TextView

        init {
            val cardView = itemView as MaterialCardView
            cardView.setFocusable(true)
            cardView.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
                    if (event.action != KeyEvent.ACTION_DOWN) {
                        return@setOnKeyListener false
                    }
                    when (keyCode) {
                        KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                            listener.onKeyboardDragToggle(this)
                            return@setOnKeyListener true
                        }

                        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> return@setOnKeyListener listener.onKeyboardMoved(
                            keyCode
                        )

                        else -> return@setOnKeyListener false
                    }
                }
/*
            val dragHandleView = itemView.findViewById<View>(R.id.cat_card_list_item_drag_handle)
            dragHandleView.setOnTouchListener { v: View, event: MotionEvent ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            listener.onDragStarted(this)
                            return@setOnTouchListener true
                        }

                        MotionEvent.ACTION_UP -> v.performClick()
                        else -> {}
                    }
                    false
                }

            titleView = itemView.findViewById<TextView>(R.id.cat_card_list_item_title)

 */
        }

        fun bind(cardNumber: Int) {
            //titleView.setText(String.format(Locale.getDefault(), "Card #%02d", cardNumber))
        }
    }
}

private class CardItemTouchHelperCallback(private val cardAdapter: CustomizerAdapter) :
    ItemTouchHelper.Callback() {

    private var dragCardView: MaterialCardView? = null
        set(value) {
            field?.isDragged = false
            field = value
            if (value != null) {
                value.isDragged = true
            }
        }

    override fun getMovementFlags(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(DRAG_FLAGS, SWIPE_FLAGS)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.getBindingAdapterPosition()
        val toPosition = target.getBindingAdapterPosition()
        cardAdapter.swap(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
            cardAdapter.cancelDrag()
            dragCardView = viewHolder.itemView as MaterialCardView
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            dragCardView = null
        }
    }

    companion object {
        private const val DRAG_FLAGS = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        private const val SWIPE_FLAGS = 0
    }
}

private interface OnKeyboardDragListener {
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder)

    fun onKeyboardDragToggle(viewHolder: RecyclerView.ViewHolder)

    fun onKeyboardMoved(keyCode: Int): Boolean
}