package me.quenchjian.presentation.edittask

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.edittask.model.AddTaskUseCase
import me.quenchjian.presentation.edittask.model.DescriptionEmptyError
import me.quenchjian.presentation.edittask.model.TitleEmptyError
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AddTaskUseCaseTest {

  private val useCase: AddTaskUseCase = AddTaskUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var taskAdded: Boolean = false
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.registerListener(object : AddTaskUseCase.Result {
      override fun onSuccess(task: Task) {
        taskAdded = true
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
  fun testTitleEmpty() {
    useCase("", "")
    assertFalse(taskAdded)
    assertEquals(TitleEmptyError, throwable)
  }

  @Test
  fun testDescriptionEmpty() {
    useCase("Title", "")
    assertFalse(taskAdded)
    assertEquals(DescriptionEmptyError, throwable)
  }

  @Test
  fun testAddTaskSuccess() {
    useCase("Title", "Description")
    assertTrue(taskAdded)
    assertNull(throwable)
  }
}
