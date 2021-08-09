package me.quenchjian.model

import java.util.UUID

data class Task(
  val id: String = UUID.randomUUID().toString(),
  var title: String = "",
  var description: String = "",
  var isCompleted: Boolean = false
)
