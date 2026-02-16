package com.github.redborsch.binding

import android.view.LayoutInflater
import android.view.ViewGroup

internal typealias ViewBindingInflate<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB
