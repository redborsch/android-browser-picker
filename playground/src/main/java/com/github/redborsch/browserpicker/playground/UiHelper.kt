package com.github.redborsch.browserpicker.playground

import android.content.Intent
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.redborsch.browserpicker.playground.databinding.ActivityMainBinding
import com.github.redborsch.browserpicker.shared.ui.BrowserListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UiHelper(
    private val viewModel: MainViewModel,
) {

    fun setUp(
        binding: ActivityMainBinding,
        coroutineScope: CoroutineScope,
    ) {
        val adapter = BrowserListAdapter(coroutineScope) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = MainViewModel.uri
                setPackage(it.packageName)
            }
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
        coroutineScope.observeState(binding, adapter)
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

    private fun CoroutineScope.observeState(
        binding: ActivityMainBinding,
        adapter: BrowserListAdapter,
    ) = launch {
        viewModel.repoData.collect { state ->
            binding.timing.text = state.fetchTime?.let { "$it ms" } ?: ""
            binding.count.text = "${state.browsers.size} browser(s)"
            adapter.browserList = state.browsers
        }
    }
}

