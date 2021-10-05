package me.quenchjian.presentation.edittask

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.quenchjian.MainActivity
import me.quenchjian.R
import me.quenchjian.appContext
import me.quenchjian.presentation.tasks.TasksFragment
import me.quenchjian.utils.hasItemCount
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EditTaskScreenTest {

  @get:Rule
  val hiltRule: HiltAndroidRule = HiltAndroidRule(this)

  private val backstack = arrayOf(TasksFragment.Key(), EditTaskFragment.Key())

  @Before
  fun setup() {
    ActivityScenario.launch<MainActivity>(MainActivity.intent(appContext, *backstack))
  }

  @Test
  fun testAddTask_goBackToTasks() {
    val expected = 3

    onView(withId(R.id.edit_text_title)).perform(clearText(), typeText("Title"), closeSoftKeyboard())
    onView(withId(R.id.edit_text_desc)).perform(clearText(), typeText("Description"), closeSoftKeyboard())
    onView(withId(R.id.fab_save)).perform(click())

    onView(withId(R.id.view_tasks)).check(matches(isDisplayed()))
    onView(withId(R.id.recycler_tasks)).check(matches(hasItemCount(expected)))
  }

  @Test
  fun testEditTask_showTitleEmpty() {
    val expected = R.string.empty_task_title

    onView(withId(R.id.edit_text_title)).perform(clearText())
    onView(withId(R.id.edit_text_desc)).perform(clearText())
    onView(withId(R.id.fab_save)).perform(click())

    onView(withId(expected)).check(matches(isDisplayed()))
  }

  @Test
  fun testEditTask_showDescriptionEmpty() {
    val expected = R.string.empty_task_description

    onView(withId(R.id.edit_text_title)).perform(clearText(), typeText("Title"), closeSoftKeyboard())
    onView(withId(R.id.edit_text_desc)).perform(clearText())
    onView(withId(R.id.fab_save)).perform(click())

    onView(withText(expected)).check(matches(isDisplayed()))
  }

  @Test
  fun testBackClick() {
    Espresso.pressBack()

    onView(withId(R.id.view_tasks)).check(matches(isDisplayed()))
  }
}
