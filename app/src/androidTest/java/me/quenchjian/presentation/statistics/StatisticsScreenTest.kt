package me.quenchjian.presentation.statistics

import androidx.core.view.GravityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.quenchjian.MainActivity
import me.quenchjian.R
import me.quenchjian.appContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class StatisticsScreenTest {

  @get:Rule
  val hiltRule: HiltAndroidRule = HiltAndroidRule(this)

  private val backstack = arrayOf(StatisticsFragment.Key())

  @Before
  fun setup() {
    ActivityScenario.launch<MainActivity>(MainActivity.intent(appContext, *backstack))
  }

  @Test
  fun testStatisticsShowed() {
    onView(withId(R.id.text_statistics_active)).check(matches(isDisplayed()))
    onView(withId(R.id.text_statistics_completed)).check(matches(isDisplayed()))
  }

  @Test
  fun testOpenDrawer_navigateToTasksScreen() {
    onView(withContentDescription(R.string.toolbar_navigation_content_description)).perform(click())
    onView(withId(R.id.view_statistics)).check(matches(isOpen(GravityCompat.START)))

    onView(withId(R.id.navigation)).perform(navigateTo(R.id.action_tasks))
    onView(withId(R.id.view_tasks)).check(matches(isDisplayed()))
  }
}
