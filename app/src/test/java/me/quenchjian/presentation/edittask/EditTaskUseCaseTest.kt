package me.quenchjian.presentation.edittask

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.edittask.usecase.DescriptionEmptyError
import me.quenchjian.presentation.edittask.usecase.EditTaskUseCase
import me.quenchjian.presentation.edittask.usecase.TitleEmptyError
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EditTaskUseCaseTest {

  private val useCase: EditTaskUseCase = EditTaskUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var updatedTask: Task? = null
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.subscribe(State.Observer(
      onSuccess = { updatedTask = it },
      onError = { throwable = it }
    ))
  }

  @AfterTest
  fun destroy() {
    useCase.dispose()
  }

  @Test
  fun testTitleEmpty() {
    useCase(Task(id = "1", title = "", description = ""))
    assertNull(updatedTask)
    assertEquals(TitleEmptyError, throwable)
  }

  @Test
  fun testDescriptionEmpty() {
    useCase(Task(id = "1", title = "Title", description = ""))
    assertNull(updatedTask)
    assertEquals(DescriptionEmptyError, throwable)
  }

  @Test
  fun testEditTaskSuccess() {
    useCase(Task(id = "1", title = "Title", description = "Description"))
    assertNotNull(updatedTask)
    assertEquals("Title", updatedTask!!.title)
    assertEquals("Description", updatedTask!!.description)
    assertNull(throwable)
  }

  @Test
  fun testEditTaskFail() {
    useCase(Task(title = "Title", description = "Description"))
    assertNull(updatedTask)
    assertNotNull(throwable)
    assertEquals("Task not found", throwable!!.message)
  }
}
