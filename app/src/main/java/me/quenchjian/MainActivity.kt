package me.quenchjian

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import me.quenchjian.navigation.FragmentKey
import me.quenchjian.navigation.Navigator
import me.quenchjian.presentation.tasks.TasksFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private lateinit var navigator: Navigator
  private var containerId: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val backstack = savedInstanceState?.backstack ?: intent.backstack ?: listOf(TasksFragment.Key())
    navigator = Navigator.init(this, createContainer(savedInstanceState), backstack)
    onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (!navigator.goBack()) {
          isEnabled = false
        }
      }
    })
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.backstack = ArrayList(navigator.getBackStack())
    outState.containerId = containerId
  }

  private fun createContainer(state: Bundle?): Int {
    containerId = state?.containerId ?: View.generateViewId()
    val lp = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )
    setContentView(FrameLayout(this).apply { id = containerId }, lp)
    return containerId
  }

  @Suppress("DEPRECATION")
  companion object {
    private const val KEY_BACKSTACK = "init-backstack"
    private val Intent.backstack: ArrayList<FragmentKey>?
      get() = getParcelableArrayListExtra(KEY_BACKSTACK)
    private var Bundle.backstack: ArrayList<FragmentKey>?
      get() = getParcelableArrayList(KEY_BACKSTACK)
      set(value) = putParcelableArrayList(KEY_BACKSTACK, value)
    private var Bundle.containerId: Int
      get() = getInt("container-id")
      set(value) = putInt("container-id", value)

    fun intent(context: Context, vararg keys: FragmentKey): Intent {
      return Intent.makeMainActivity(ComponentName(context, MainActivity::class.java))
        .putParcelableArrayListExtra("init-backstack", arrayListOf(*keys))
    }
  }
}
