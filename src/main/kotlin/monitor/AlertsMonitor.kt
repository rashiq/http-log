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
    launch {
      ticker {
        synchronized(buffer) {
          recalculateBuffer()
          checkAlerting()
        }
      }
    }

    for (event in bus.subscribe<LogEvent>()) {
      synchronized(buffer) {
        buffer.add(LocalDateTime.now())
      }
    }
  }

  private fun recalculateBuffer() {
    val now = LocalDateTime.now()
    var tail = buffer.lastOrNull()
    while (tail != null && Duration.between(tail, now).toMinutes() >= configuration.alertingWindow) {
      buffer.removeLast()
      tail = buffer.lastOrNull()
    }
  }

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