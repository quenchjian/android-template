package me.quenchjian.presentation.taskdetail

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.taskdetail.usecase.DeleteTaskUseCase
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeleteTaskUseCaseTest {

  private val useCase: DeleteTaskUseCase = DeleteTaskUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var taskDeleted: Boolean = false
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.subscribe(State.Observer(
      onSuccess = { taskDeleted = true },
      onError = { throwable = it }
    ))
  }

  @AfterTest
  fun destroy() {
    useCase.dispose()
  }

  @Test
  fun testSuccess() {
    useCase("1")
    assertTrue(taskDeleted)
    assertNull(throwable)
  }
}
