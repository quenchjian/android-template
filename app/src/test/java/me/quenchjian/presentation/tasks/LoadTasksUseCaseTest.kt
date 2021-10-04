package me.quenchjian.presentation.tasks

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.model.Task
import me.quenchjian.presentation.common.model.State
import me.quenchjian.presentation.tasks.usecase.LoadTasksUseCase
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LoadTasksUseCaseTest {

  private val useCase: LoadTasksUseCase = LoadTasksUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var tasks: List<Task> = emptyList()
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.subscribe(State.Observer(
      onSuccess = { tasks = it },
      onError = { throwable = it }
    ))
  }

  @AfterTest
  fun destroy() {
    useCase.dispose()
  }

  @Test
  fun testSuccess() {
    useCase(true, TasksScreen.Filter.ALL)
    assertNotNull(tasks)
    assertNull(throwable)
  }
}
