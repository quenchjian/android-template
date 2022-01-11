package me.quenchjian.presentation.taskdetail

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.presentation.taskdetail.model.DeleteTaskUseCase
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
    useCase.registerListener(object : DeleteTaskUseCase.Result {
      override fun onSuccess() {
        taskDeleted = true
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
    useCase("1")
    assertTrue(taskDeleted)
    assertNull(throwable)
  }
}
