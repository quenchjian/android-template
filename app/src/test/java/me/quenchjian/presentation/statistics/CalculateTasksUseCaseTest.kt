package me.quenchjian.presentation.statistics

import me.quenchjian.concurrent.TestScheduler
import me.quenchjian.data.FakeTaskRepository
import me.quenchjian.presentation.statistics.model.CalculateTasksUseCase
import me.quenchjian.presentation.statistics.model.Statistics
import me.quenchjian.webservice.FakeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CalculateTasksUseCaseTest {

  private val useCase: CalculateTasksUseCase = CalculateTasksUseCase(TestScheduler(), FakeApi(), FakeTaskRepository())

  private var statistics: Statistics? = null
  private var throwable: Throwable? = null

  @BeforeTest
  fun setup() {
    useCase.registerListener(object : CalculateTasksUseCase.Result {
      override fun onSuccess(statistics: Statistics) {
        this@CalculateTasksUseCaseTest.statistics = statistics
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
  fun testEnableReload() {
    useCase(true)
    assertNotNull(statistics)
    assertEquals(100, statistics!!.activePercent)
    assertEquals(0, statistics!!.completedPercent)
    assertNull(throwable)
  }

  @Test
  fun testDisableReload() {
    useCase(false)
    assertNotNull(statistics)
    assertEquals(100, statistics!!.activePercent)
    assertEquals(0, statistics!!.completedPercent)
    assertNull(throwable)
  }
}
