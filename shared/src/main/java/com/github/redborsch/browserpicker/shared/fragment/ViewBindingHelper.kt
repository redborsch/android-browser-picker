package com.github.redborsch.browserpicker.shared.fragment

import android.app.Activity
import android.view.View
import androidx.viewbinding.ViewBinding

interface ViewBindingFragment<VB : ViewBinding> {

    fun VB.setUp()
}

/**
 * To be used in `Fragment.onCreateView`. The fragment must implement [ViewBindingFragment].
 *
 * ```
 *     override fun onCreateView(
 *         inflater: LayoutInflater,
 *         container: ViewGroup?,
 *         savedInstanceState: Bundle?
 *     ): View = attachBinding(MyBinding.inflate(inflater, container, false))
 * ```
 */
fun <VB : ViewBinding> ViewBindingFragment<VB>.attachBinding(binding: VB): View {
    with(binding) {
        setUp()
    }
    return binding.root
}

fun Activity.setContentView(binding: ViewBinding) {
    setContentView(binding.root)
}