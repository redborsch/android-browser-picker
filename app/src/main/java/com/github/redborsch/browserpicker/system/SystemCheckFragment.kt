package com.github.redborsch.browserpicker.system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.redborsch.browserpicker.databinding.FragmentSystemCheckBinding
import com.github.redborsch.browserpicker.shared.fragment.ViewBindingFragment
import com.github.redborsch.browserpicker.shared.fragment.attachBinding
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class SystemCheckFragment : Fragment(), ViewBindingFragment<FragmentSystemCheckBinding> {

    private val viewModel: SystemCheckViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = attachBinding(
        FragmentSystemCheckBinding.inflate(inflater, container, false)
    )

    override fun FragmentSystemCheckBinding.setUp() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isDefault.collect {
                    val fragmentClass = when (it) {
                        null -> ProgressFragment::class
                        false -> MakeDefaultFragment::class
                        true -> AllSetFragment::class
                    }
                    replaceCurrentFragment(fragmentClass, fragmentHost)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshDefaultState()
    }

    private fun <F : Fragment> replaceCurrentFragment(fragmentClass: KClass<F>, container: View) {
        val fm = childFragmentManager
        val tag = fragmentClass.qualifiedName
        if (fm.findFragmentByTag(tag) != null) {
            return
        }
        fm.commitNow(allowStateLoss = true) {
            replace(
                container.id,
                fragmentClass.java.getDeclaredConstructor().newInstance(),
                tag
            )
        }
    }
}
