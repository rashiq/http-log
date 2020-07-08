package model

import java.time.LocalDateTime

open class Event

data class LogEvent(val log: LogLine) : Event()
data class LogErrorEvent(val exception: Exception) : Event()

data class RenderUiEvent(
  val totalRequests: Int,
  val totalDataTransferred: Int,
  val dataTransferred: Int,
  val topRequestsBySection: List<Map.Entry<String, Int>>,
  val successfulRequests: Int,
  val redirectRequests: Int,
  val clientErrorRequests: Int,
  val serverErrorsRequests: Int
) : Event()

open class RenderAlertEvent : Event()
class RenderAlertTriggeredEvent(val hitsPerSecond: Int, val triggeredTime: LocalDateTime) : RenderAlertEvent()
class RenderAlertRecoveredEvent(val hitsPerSecond: Int, val recoveredTime: LocalDateTime) : RenderAlertEvent()