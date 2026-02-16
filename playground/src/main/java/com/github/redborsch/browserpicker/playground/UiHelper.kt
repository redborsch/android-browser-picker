package com.github.redborsch.browserpicker.playground

import android.annotation.SuppressLint
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.playground.databinding.ActivityMainBinding
import com.github.redborsch.browserpicker.shared.system.createViewIntent
import com.github.redborsch.browserpicker.shared.ui.BrowserListAdapter
import com.github.redborsch.lifecycle.launchOnEachStart

class UiHelper(
    private val viewModel: MainViewModel,
) {

    fun setUp(
        binding: ActivityMainBinding,
        lifecycleOwner: LifecycleOwner,
    ) {
        val adapter = BrowserListAdapter(lifecycleOwner) {
            val intent = createViewIntent(MainViewModel.uri, it.packageName)
            binding.root.context.startActivity(intent)
        }
        with(binding) {
            setUpRepoSelector(repoSelector)
            browserList.layoutManager = LinearLayoutManager(
                binding.root.context,
                RecyclerView.VERTICAL,
                false,
            )
            browserList.adapter = adapter
        }
        lifecycleOwner.observeState(binding, adapter)
    }

    private fun setUpRepoSelector(radioGroup: RadioGroup) {
        val repos = BrowserListRepoType.entries

        repos.forEach {
            val radioButton = RadioButton(radioGroup.context).apply {
                id = it.ordinal
                text = it.name
            }
            radioGroup.addView(radioButton)
        }
        radioGroup.check(viewModel.repoType.ordinal)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.repoType = repos[checkedId]
        }
    }

    @SuppressLint("SetTextI18n")
    private fun LifecycleOwner.observeState(
        binding: ActivityMainBinding,
        adapter: BrowserListAdapter,
    ) = launchOnEachStart {
        viewModel.repoData.collect { state ->
            binding.timing.text = state.fetchTime?.let { "$it ms" } ?: ""
            binding.count.text = "${state.browsers.size} browser(s)"
            adapter.browserList = state.browsers
        }
    }
}
