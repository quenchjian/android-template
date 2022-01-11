package me.quenchjian.presentation.statistics.model

import kotlin.math.roundToInt

data class Statistics(val active: Float, val completed: Float) {
  val isEmpty = active == 0f && completed == 0f
  val activePercent = if (isEmpty) 0 else active.roundToInt()
  val completedPercent = if (isEmpty) 0 else 100 - activePercent
}
