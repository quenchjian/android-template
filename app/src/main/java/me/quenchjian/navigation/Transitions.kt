package me.quenchjian.navigation

import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionSet

enum class Transitions {
  NONE, HORIZONTAL, VERTICAL,
  ;

  val enter: Transition
    get() = when (this) {
      NONE -> TransitionSet()
      HORIZONTAL -> TransitionSet()
        .addTransition(Slide(Gravity.END).apply { mode = Slide.MODE_IN })
        .addTransition(Fade(Fade.IN))
        .setDuration(250)
        .setInterpolator(AccelerateDecelerateInterpolator())
      VERTICAL -> TransitionSet()
        .addTransition(Slide(Gravity.BOTTOM).apply { mode = Slide.MODE_IN })
        .addTransition(Fade(Fade.IN))
        .setDuration(250)
        .setInterpolator(AccelerateDecelerateInterpolator())
    }

  val exit: Transition
    get() = when (this) {
      NONE -> TransitionSet()
      HORIZONTAL -> TransitionSet()
        .addTransition(Slide(Gravity.END).apply { mode = Slide.MODE_OUT })
        .addTransition(Fade(Fade.OUT))
        .setDuration(250)
        .setInterpolator(AccelerateDecelerateInterpolator())
      VERTICAL -> TransitionSet()
        .addTransition(Slide(Gravity.BOTTOM).apply { mode = Slide.MODE_OUT })
        .addTransition(Fade(Fade.IN))
        .setDuration(250)
        .setInterpolator(AccelerateDecelerateInterpolator())
    }
}
