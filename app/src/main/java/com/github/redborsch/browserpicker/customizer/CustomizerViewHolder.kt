package com.github.redborsch.browserpicker.customizer

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.customizer.model.CustomizerItem
import com.github.redborsch.browserpicker.databinding.ItemCustomizerEntryBinding
import com.github.redborsch.browserpicker.shared.ui.BrowserEntryViewHolder
import com.github.redborsch.recyclerview.DragController
import com.github.redborsch.recyclerview.DragHandlerTouchListener
import com.github.redborsch.recyclerview.DragKeyListener
import com.github.redborsch.recyclerview.DraggableViewHolder

@SuppressLint("ClickableViewAccessibility")
class CustomizerViewHolder(
    private val binding: ItemCustomizerEntryBinding,
    dragController: DragController,
) : RecyclerView.ViewHolder(binding.root), DraggableViewHolder {

    private val wrappedViewHolder = BrowserEntryViewHolder(binding.browserEntry, null)

    private var item: CustomizerItem? = null

    init {
        binding.setUp()

        binding.dragHandle.setOnTouchListener(
            DragHandlerTouchListener(this, dragController)
        )

        binding.cardView.setOnKeyListener(DragKeyListener(this, dragController))
    }

    fun bind(item: CustomizerItem, lifecycleOwner: LifecycleOwner) {
        this.item = item
        wrappedViewHolder.bind(item.browserData, lifecycleOwner)
        bindVisibility(item)
    }

    fun recycle() {
        item = null
        wrappedViewHolder.recycle()
    }

    fun ItemCustomizerEntryBinding.setUp() {
        browserEntry.root.background = null
        visibilityIndicator.setOnClickListener {
            toggleVisibility()
        }
    }

    private fun bindVisibility(item: CustomizerItem) {
        @DrawableRes val drawableResId: Int
        @StringRes val contentDescriptionResId: Int
        val alpha: Float
        if (item.isVisible) {
            drawableResId = R.drawable.outline_visibility_24
            contentDescriptionResId = R.string.customizer_entry_hide
            alpha = 1.0f
        } else {
            drawableResId = R.drawable.outline_visibility_off_24
            contentDescriptionResId = R.string.customizer_entry_show
            alpha = 0.25f
        }
        with(binding.visibilityIndicator) {
            setImageDrawable(
                AppCompatResources.getDrawable(context, drawableResId)
            )
            contentDescription = context.getString(contentDescriptionResId)
        }
        with(binding.browserEntry) {
            browserIcon.alpha = alpha
            browserName.isEnabled = item.isVisible
        }
    }

    private fun toggleVisibility() {
        val data = item ?: return
        data.toggleVisibility()
        bindVisibility(data)
    }

    override fun setIsDragged(dragged: Boolean) {
        binding.cardView.isDragged = dragged
    }
}
