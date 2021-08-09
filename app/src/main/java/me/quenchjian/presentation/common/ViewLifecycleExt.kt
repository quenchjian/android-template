package me.quenchjian.presentation.common

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun <V : Screen.View> AppCompatActivity.createView(factory: (LayoutInflater) -> V) =
  ActivityViewDelegate(this, factory)

fun <V : Screen.View> Fragment.createView(factory: (View) -> V) = FragmentViewDelegate(this, factory)
