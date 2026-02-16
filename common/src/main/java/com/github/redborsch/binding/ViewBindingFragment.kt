package com.github.redborsch.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ViewBindingFragment<VB : ViewBinding>(
    private val factory: ViewBindingInflate<VB>,
) : Fragment() {

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return factory(inflater, container, false).apply {
            setUp()
        }.root
    }

    protected abstract fun VB.setUp()
}
