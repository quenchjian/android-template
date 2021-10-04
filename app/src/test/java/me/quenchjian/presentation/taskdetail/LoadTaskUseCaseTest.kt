package me.quenchjian.presentation.taskdetail

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.taskdetail.usecase.DeleteTaskUseCase
import me.quenchjian.presentation.taskdetail.usecase.LoadTaskUseCase
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoadTaskUseCaseTest {

  private val useCase: LoadTaskUseCase = LoadTaskUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var task: Task? = null
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.subscribe(State.Observer(
      onSuccess = { task = it },
      onError = { throwable = it }
    ))
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
