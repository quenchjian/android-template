package me.quenchjian.presentation.common.view

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun <V : MvcView> AppCompatActivity.createView(factory: (LayoutInflater) -> V) =
  ActivityViewDelegate(this, factory)

fun <V : MvcView> Fragment.createView(factory: (View) -> V) = FragmentViewDelegate(this, factory)
