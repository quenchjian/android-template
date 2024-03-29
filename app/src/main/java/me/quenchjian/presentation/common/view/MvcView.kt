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
import com.google.android.material.snackbar.Snackbar

interface MvcView {
  val root: View
  val context: Context get() = root.context
  fun attach(@NonNull saved: Bundle) {}
  fun detach(@NonNull state: Bundle) {}
  fun showError(msg: String) {
    Snackbar.make(root, msg, Snackbar.LENGTH_SHORT).show()
  }
  fun string(@StringRes id: Int, vararg obj: Any): String = context.getString(id, *obj)
  fun drawable(@DrawableRes id: Int): Drawable = ContextCompat.getDrawable(context, id)!!
  fun color(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)
}
