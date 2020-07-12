package monitor

import EventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Configuration
import model.LogEvent
import model.RenderAlertRecoveredEvent
import model.RenderAlertTriggeredEvent
import ticker
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class AlertsMonitor(
  private val configuration: Configuration,
  private val bus: EventBus
) : Monitor {

  private val buffer = LinkedList<LocalDateTime>()
  private var alerted = false

  override fun monitor() = GlobalScope.launch(Dispatchers.Default) {

    // launch a ticker inside coroutine that checks the buffer
    // for the current traffic every second
    launch {
      ticker {
        synchronized(buffer) {
          recalculateBuffer()
          checkAlerting()
        }
      }
    }

    // Add a timestamp for every log entry we receive
    for (event in bus.subscribe<LogEvent>()) {
      synchronized(buffer) {
        buffer.add(LocalDateTime.now())
      }
    }
  }

  /**
   * Remove all elements inside [buffer] that are older
   * than the specified [Configuration.alertingWindow].
   */
  private fun recalculateBuffer() {
    val now = LocalDateTime.now()
    var tail = buffer.lastOrNull()
    while (tail != null && Duration.between(tail, now).toMinutes() >= configuration.alertingWindow) {
      buffer.removeLast()
      tail = buffer.lastOrNull()
    }
  }

  /**
   * Check whether we need to dispatch a [RenderAlertTriggeredEvent]
   * in case we register more events than specified by the alerting
   * threshold [Configuration.alertingThreshold], or whether we need to
   * dispatch a [RenderAlertRecoveredEvent] in case we've recovered from
   * an alert.
   */
  private fun checkAlerting() {
    val hitsThreshold = configuration.alertingThreshold * 60 * configuration.alertingWindow
    val hitsPerSecond = buffer.size / 60 / configuration.alertingWindow
    val triggeredTime = LocalDateTime.now()

    if (buffer.size > hitsThreshold) {
      if (!alerted) {
        bus.dispatch(
          RenderAlertTriggeredEvent(hitsPerSecond, triggeredTime)
        )
        alerted = true
      }
    } else {
      if (alerted) {
        bus.dispatch(
          RenderAlertRecoveredEvent(hitsPerSecond, triggeredTime)
        )
        alerted = false
      }
    }
  }
}