package com.github.redborsch.browserpicker.customizer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.databinding.ItemCustomizerEntryBinding
import com.github.redborsch.browserpicker.shared.ui.BrowserEntryViewHolder

class CustomizerViewHolder(
    private val binding: ItemCustomizerEntryBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val wrappedViewHolder = BrowserEntryViewHolder(binding.browserEntry, null)

    private var data: CustomizerData? = null

    init {
        binding.browserEntry.root.background = null
        binding.visibility.setOnClickListener {
            toggleVisibility()
        }
    }

    fun bind(data: CustomizerData, lifecycleOwner: LifecycleOwner) {
        this.data = data
        wrappedViewHolder.bind(data.browserData, lifecycleOwner)
        bindVisibility(data)
    }

    fun recycle() {
        data = null
        wrappedViewHolder.recycle()
    }

    private fun bindVisibility(data: CustomizerData) {
        @DrawableRes val drawableResId: Int
        @StringRes val contentDescriptionResId: Int
        val alpha: Float
        if (data.isVisible) {
            drawableResId = R.drawable.outline_visibility_24
            contentDescriptionResId = R.string.customizer_entry_hide
            alpha = 1.0f
        } else {
            drawableResId = R.drawable.outline_visibility_off_24
            contentDescriptionResId = R.string.customizer_entry_show
            alpha = 0.25f
        }
        val context = binding.root.context
        with(binding.visibility) {
            setImageDrawable(
                AppCompatResources.getDrawable(context, drawableResId)
            )
            contentDescription = context.getString(contentDescriptionResId)
        }
        with (binding.browserEntry) {
            browserIcon.alpha = alpha
            browserName.isEnabled = data.isVisible
        }
    }

    private fun toggleVisibility() {
        val data = data ?: return
        data.toggleVisibility()
        bindVisibility(data)
    }
}
