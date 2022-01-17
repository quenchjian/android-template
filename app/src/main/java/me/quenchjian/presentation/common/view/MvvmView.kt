package me.quenchjian.presentation.common.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar

abstract class MvvmView<VM : ViewModel> {
  abstract val root: View
  val context: Context get() = root.context
  var error: String = ""
    set(value) {
      field = value
      if (value.isNotEmpty()) {
        Snackbar.make(root, value, Snackbar.LENGTH_SHORT).show()
      }
    }

  fun attach(@NonNull saved: Bundle) {}
  fun detach(@NonNull state: Bundle) {}
  fun string(@StringRes id: Int, vararg obj: Any): String = context.getString(id, *obj)
  fun drawable(@DrawableRes id: Int): Drawable = ContextCompat.getDrawable(context, id)!!
  fun color(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)
}
