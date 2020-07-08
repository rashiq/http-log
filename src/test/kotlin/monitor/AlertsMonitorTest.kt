package monitor

import EventBus
import generateLogEvents
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import model.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

internal class AlertsMonitorTest {

  private lateinit var bus: EventBus
  private lateinit var alertsMonitor: AlertsMonitor

  @Before
  fun setUp() {
    /**
     * Alert threshold set to 1 request/second
     * with an alert window of 1 minute
     */
    val configuration = Configuration(
      logFile = mockk(),
      alertingThreshold = 1,
      alertingWindow = 1
    )

    bus = EventBus()
    alertsMonitor = AlertsMonitor(configuration, bus)
  }

  private fun dispatchLogEvents() {
    val logs = generateLogEvents(100)
    logs.forEach {
      bus.dispatch(LogEvent(it))
      Thread.sleep(10)
    }
  }

  @Test(timeout = 1000 * 60 * 2)
  fun `test generating an alert by creating 100 logs`() {
    val subscription = bus.subscribe<RenderAlertEvent>()
    alertsMonitor.monitor()
    dispatchLogEvents()

    runBlocking {
      for (event in subscription) {
        assert(event is RenderAlertTriggeredEvent)
        return@runBlocking
      }
    }
  }

  @Test(timeout = 1000 * 60 * 2)
  fun `test recover from alert after 1 minute`() {
    val subscription = bus.subscribe<RenderAlertEvent>()
    alertsMonitor.monitor()
    dispatchLogEvents()

    runBlocking {
      var triggered = false
      var triggeredTime: LocalDateTime? = null
      for (event in subscription) {
        if (!triggered) {
          triggered = event is RenderAlertTriggeredEvent
          triggeredTime = LocalDateTime.now()
        } else {
          assert(event is RenderAlertRecoveredEvent)
          assertEquals(Duration.between(triggeredTime, LocalDateTime.now()).toMinutes().toInt(), 1)
          return@runBlocking
        }
      }
    }
  }
}