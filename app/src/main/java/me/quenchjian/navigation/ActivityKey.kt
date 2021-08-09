package me.quenchjian.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable

abstract class ActivityKey : NavKey, Parcelable {

  fun createIntent(context: Context): Intent {
    val intent = instantiateIntent(context)
    val extra = intent.extras ?: Bundle()
    extra.putParcelable(KEY, this)
    return intent.replaceExtras(extra)
  }

  abstract fun instantiateIntent(context: Context): Intent
  open fun createOption(context: Context): Bundle? = null

  companion object {
    const val KEY = "activity-key"
  }
}
