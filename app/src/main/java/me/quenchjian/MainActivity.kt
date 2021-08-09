package me.quenchjian

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import me.quenchjian.navigation.Navigator
import me.quenchjian.presentation.tasks.TasksFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var navigatorProvider: Navigator.Provider
  private lateinit var navigator: Navigator
  private var containerId: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    navigator = navigatorProvider.get(this, createContainer(savedInstanceState))
    navigator.setBackStack(listOf(TasksFragment.Key()))
  }

  override fun onBackPressed() {
    if (!navigator.goBack()) {
      super.onBackPressed()
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.saveId(containerId)
  }

  private fun createContainer(state: Bundle?): Int {
    containerId = state?.getId() ?: View.generateViewId()
    val lp = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )
    setContentView(FrameLayout(this).apply { id = containerId }, lp)
    return containerId
  }

  companion object {
    private fun Bundle?.getId(): Int? = this?.getInt("container-id")
    private fun Bundle.saveId(id: Int) = putInt("container-id", id)
  }
}
