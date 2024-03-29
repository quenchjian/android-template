package me.quenchjian.presentation.taskdetail

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.taskdetail.model.ChangeTaskStateUseCase
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChangeTaskStateUseCaseTest {

  private val useCase: ChangeTaskStateUseCase = ChangeTaskStateUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var updatedTask: Task? = null
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.registerListener(object : ChangeTaskStateUseCase.Result {
      override fun onSuccess(task: Task) {
        updatedTask = task
      }

      override fun onError(t: Throwable) {
        throwable = t
      }
    })
  }

  @AfterTest
  fun destroy() {
    useCase.dispose()
  }

  @Test
  fun testSuccess() {
    useCase(Task("1", "", ""), true)
    assertNotNull(updatedTask)
    assertTrue(updatedTask!!.isCompleted)
    assertNull(throwable)
  }

  @Test
  fun testFailure() {
    useCase(Task("10", "", ""), true)
    assertNull(updatedTask)
    assertNotNull(throwable)
    assertEquals("Task not found", throwable!!.message)
  }
}
