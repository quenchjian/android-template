package me.quenchjian.presentation.tasks

import android.widget.ListView
import androidx.core.view.GravityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.quenchjian.MainActivity
import me.quenchjian.R
import me.quenchjian.appContext
import me.quenchjian.utils.atPosition
import me.quenchjian.utils.clickChild
import me.quenchjian.utils.hasItemCount
import org.hamcrest.Matchers.anything
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TasksScreenTest {

  @get:Rule
  val hiltRule: HiltAndroidRule = HiltAndroidRule(this)

  private val backstack = arrayOf(TasksFragment.Key())

  @Before
  fun setup() {
    ActivityScenario.launch<MainActivity>(MainActivity.intent(appContext, *backstack))
  }

  @Test
  fun testDrawerOpen_navigateToStatisticsScreen() {
    onView(withContentDescription(R.string.toolbar_navigation_content_description)).perform(click())
    onView(withId(R.id.view_tasks)).check(matches(isOpen(GravityCompat.START)))

    val expected = R.id.view_statistics
    onView(withId(R.id.navigation)).perform(navigateTo(R.id.action_statistics))
    onView(withId(expected)).check(matches(isDisplayed()))
  }

  @Test
  fun testFilterMenuShowed_filterCompleted() {
    // check popup menu showed
    onView(withId(R.id.action_filter)).perform(click())
    onView(isAssignableFrom(ListView::class.java)).check(matches(isDisplayed()))

    // click filter complete in popup menu
    onData(anything()).inAdapterView(isAssignableFrom(ListView::class.java)).atPosition(2).perform(click())
    onView(withId(R.id.recycler_tasks)).check(matches(hasItemCount(0)))
  }

  @Test
  fun testTaskClick_navigateToTaskScreen() {
    onView(withId(R.id.recycler_tasks)).perform(clickChild())
    onView(withId(R.id.view_task)).check(matches(isDisplayed()))
  }

  @Test
  fun testTaskCompleteClick_showTaskCompleted() {
    onView(withId(R.id.recycler_tasks)).perform(clickChild(0, R.id.checkbox_task_state))
    onView(withId(R.id.recycler_tasks)).check(matches(atPosition(0, R.id.checkbox_task_state, isChecked())))
  }

  @Test
  fun testAddClick_navigateToEditTaskScreen() {
    onView(withId(R.id.fab_add)).perform(click())
    onView(withId(R.id.view_edit_task)).check(matches(isDisplayed()))
  }
}
