package me.quenchjian.presentation.taskdetail

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.taskdetail.model.LoadTaskUseCase
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LoadTaskUseCaseTest {

  private val useCase: LoadTaskUseCase = LoadTaskUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var task: Task? = null
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.registerListener(object : LoadTaskUseCase.Result {
      override fun onSuccess(task: Task) {
        this@LoadTaskUseCaseTest.task = task
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
    useCase("1", true)
    assertNotNull(task)
    assertNull(throwable)
  }

  @Test
  fun testFail() {
    useCase("10", false)
    assertNull(task)
    assertNotNull(throwable)
    assertEquals("Task not found", throwable!!.message)
  }
}
