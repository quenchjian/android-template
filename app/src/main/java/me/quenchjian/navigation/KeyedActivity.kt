package me.quenchjian.navigation

import androidx.appcompat.app.AppCompatActivity

abstract class KeyedActivity: AppCompatActivity() {

  fun <KEY : ActivityKey> getKey(): KEY {
    return checkNotNull(intent.getParcelableExtra(ActivityKey.KEY)) { "The key provided as activity argument should never null!" }
  }
}
