package me.quenchjian.presentation.edittask

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.edittask.usecase.AddTaskUseCase
import me.quenchjian.presentation.edittask.usecase.DescriptionEmptyError
import me.quenchjian.presentation.edittask.usecase.TitleEmptyError
import me.quenchjian.webservice.FakeApi
import me.quenchjian.webservice.WebApi
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
    useCase.subscribe(State.Observer(
      onSuccess = { taskAdded = true },
      onError = { throwable = it }
    ))
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
