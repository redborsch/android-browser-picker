package com.github.redborsch.browserpicker.setup

import androidx.fragment.app.activityViewModels
import com.github.redborsch.browserpicker.databinding.FragmentMainSetupBinding
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.binding.ViewBindingFragment
import com.github.redborsch.fragment.replaceCurrentFragment
import com.github.redborsch.lifecycle.launchOnEachStart

class SetupFragment : ViewBindingFragment<FragmentMainSetupBinding>(
    FragmentMainSetupBinding::inflate
) {

    private val viewModel: SetupViewModel by activityViewModels()

    override fun FragmentMainSetupBinding.setUp() {
        viewLifecycleOwner.launchOnEachStart {
            viewModel.isDefault.collect {
                val fragmentClass = when (it) {
                    null -> ProgressFragment::class
                    false -> MakeDefaultFragment::class
                    true -> AllSetFragment::class
                }
                childFragmentManager.replaceCurrentFragment(fragmentClass, fragmentHost)
            }
        }
    }
}
