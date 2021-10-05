package me.quenchjian.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.BoundedDiagnosingMatcher
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

private val recyclerViewMatcher = allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())

fun atPosition(position: Int = 0, @IdRes childId: Int = View.NO_ID, matcher: Matcher<View>): Matcher<View> {
  return RecyclerItemView(position, childId, matcher)
}
fun hasItemCount(count: Int): Matcher<View> {
  return RecyclerViewItemCount(count)
}
fun clickChild(position: Int = 0, @IdRes childId: Int = View.NO_ID): ViewAction {
  return RecyclerViewClickAction(position, childId)
}

private class RecyclerItemView(
  private val position: Int,
  private val childId: Int,
  private val matcher: Matcher<View>,
) : BoundedDiagnosingMatcher<View, RecyclerView>(RecyclerView::class.java) {

  override fun describeMoreTo(description: Description) {
    description.appendText("get item at $position ")
    matcher.describeTo(description)
  }

  override fun matchesSafely(view: RecyclerView, mismatchDescription: Description): Boolean {
    val holder = view.findViewHolderForAdapterPosition(position) ?: return false
    val viewToMatch = when (childId) {
      View.NO_ID -> holder.itemView
      else -> holder.itemView.findViewById(childId)
    }
    return matcher.matches(viewToMatch)
  }
}

private class RecyclerViewItemCount(
  private val count: Int,
) : BoundedDiagnosingMatcher<View, RecyclerView>(RecyclerView::class.java) {

  override fun describeMoreTo(description: Description) {
    description.appendText("RecyclerAdapter.getItemCount() to be ").appendValue(count)
  }

  override fun matchesSafely(view: RecyclerView, mismatchDescription: Description): Boolean {
    val itemCount = view.adapter?.itemCount ?: 0
    mismatchDescription
      .appendText("RecyclerAdapter.getItemCount() was ")
      .appendValue(itemCount)
    return itemCount == count
  }
}

private class RecyclerViewClickAction(
  private val position: Int,
  private val childId: Int,
) : ViewAction {

  override fun getConstraints(): Matcher<View> = recyclerViewMatcher

  override fun getDescription(): String {
    return when (childId) {
      View.NO_ID -> "click on item at position: $position"
      else -> "click $childId on item at position: $position"
    }
  }

  override fun perform(uiController: UiController, view: View) {
    ScrollToPositionViewAction(position).perform(uiController, view)
    uiController.loopMainThreadUntilIdle()

    val recyclerView = view as RecyclerView
    val holder = recyclerView.findViewHolderForAdapterPosition(position) ?: throw PerformException.Builder()
      .withActionDescription(this.toString())
      .withViewDescription(HumanReadables.describe(view))
      .withCause(IllegalStateException("No view holder at position: $position"))
      .build()

    val itemView: View = when (childId) {
      View.NO_ID -> holder.itemView
      else -> holder.itemView.findViewById(childId)
    }
    click().perform(uiController, itemView)
  }
}

private class ScrollToPositionViewAction(private val position: Int) : ViewAction {

  override fun getConstraints(): Matcher<View> = recyclerViewMatcher

  override fun getDescription(): String {
    return "scroll RecyclerView to position: $position"
  }

  override fun perform(uiController: UiController, view: View) {
    (view as RecyclerView).scrollToPosition(position)
    uiController.loopMainThreadUntilIdle()
  }
}
