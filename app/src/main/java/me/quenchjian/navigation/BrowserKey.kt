package me.quenchjian.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.parcelize.Parcelize

@Parcelize
class BrowserKey(private val url: String) : ActivityKey() {
  override fun instantiateIntent(context: Context): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(url))
  }
}
