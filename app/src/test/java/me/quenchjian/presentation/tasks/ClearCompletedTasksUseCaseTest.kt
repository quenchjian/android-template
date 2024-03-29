package me.quenchjian.presentation.tasks

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.tasks.model.ClearCompletedTasksUseCase
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClearCompletedTasksUseCaseTest {

  private val useCase: ClearCompletedTasksUseCase =
    ClearCompletedTasksUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var activeTasks: List<Task> = emptyList()
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.registerListener(object : ClearCompletedTasksUseCase.Result {
      override fun onSuccess(activeTasks: List<Task>) {
        this@ClearCompletedTasksUseCaseTest.activeTasks = activeTasks
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
    useCase()
    assertEquals(2, activeTasks.size)
    assertNull(throwable)
  }
}
