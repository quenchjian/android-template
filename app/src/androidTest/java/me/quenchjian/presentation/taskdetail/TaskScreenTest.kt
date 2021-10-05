package me.quenchjian.presentation.taskdetail

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.quenchjian.MainActivity
import me.quenchjian.R
import me.quenchjian.appContext
import me.quenchjian.presentation.tasks.TasksFragment
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TaskScreenTest {

  @get:Rule
  val hiltRule: HiltAndroidRule = HiltAndroidRule(this)

  private val backstack = arrayOf(TasksFragment.Key(), TaskFragment.Key("1"))

  @Before
  fun setup() {
    ActivityScenario.launch<MainActivity>(MainActivity.intent(appContext, *backstack))
  }

  @Test
  fun testBackClick_navigateToTasksScreen() {
    val expected = R.id.view_tasks

    onView(withContentDescription(R.string.toolbar_navigation_content_description)).perform(click())

    onView(withId(expected)).check(matches(isDisplayed()))
  }

  @Test
  fun testDeleteClick_showTaskDeleted() {
    val expected = R.string.no_data

    onView(withId(R.id.action_delete)).perform(click())

    onView(withId(R.id.text_task_title)).check(matches(withText(expected)))
    onView(withId(R.id.text_task_description)).check(matches(not(isDisplayed())))
  }

  @Test
  fun testEditClick_navigateToEditTaskScreen() {
    val expected = R.id.view_edit_task

    onView(withId(R.id.fab_edit)).perform(click())

    onView(withId(expected)).check(matches(isDisplayed()))
  }

  @Test
  fun testCheckBoxClick_showTaskCompleted() {
    onView(withId(R.id.checkbox_task_state)).perform(click())

    onView(withId(R.id.checkbox_task_state)).check(matches(isChecked()))
  }
}
