package ui

import EventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.RenderAlertEvent
import model.RenderAlertRecoveredEvent
import model.RenderAlertTriggeredEvent
import model.RenderUiEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Terminal(private val bus: EventBus) {

  private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  private val renderLock = ReentrantLock()

  /**
   * Renders UI and Alert events in their own respective coroutine.
   * In oder to coordinate write to the console we're using a [ReentrantLock],
   * to make sure we're not rendering multiple events at the same time,
   * potentially making the output unreadable.
   *
   * We need to do that because the UI events and the Alert events operate
   * independently of each other.
   */
  suspend fun render() = withContext(Dispatchers.Default) {
    launch {
      for (event in bus.subscribe<RenderUiEvent>()) {
        renderLock.withLock {
          renderStatistics(event)
        }
      }
    }
    launch {
      for (event in bus.subscribe<RenderAlertEvent>()) {
        renderLock.withLock {
          renderAlerts(event)
        }
      }
    }
  }

  private fun renderAlerts(event: RenderAlertEvent) {
    clearScreen()
    val message = when (event) {
      is RenderAlertTriggeredEvent -> {
        val hitsPerSecond = event.hitsPerSecond
        val dateTime = event.triggeredTime.format(dateTimeFormat)
        "High traffic generated an alert - $hitsPerSecond hits/s, triggered at $dateTime"
      }
      is RenderAlertRecoveredEvent -> {
        val dateTime = event.recoveredTime.format(dateTimeFormat)
        val hitsPerSecond = event.hitsPerSecond
        "Alert Recovered - traffic is down to $hitsPerSecond hits/s, triggered at $dateTime"
      }
      else -> ""
    }
    listOf(
      "======================= Alert =======================",
      message
    ).forEach(::println)
    repeat(2) { println() }
  }

  private fun renderStatistics(event: RenderUiEvent) {
    clearScreen()
    with(event) {
      listOf(
        "================ ${LocalDateTime.now().format(dateTimeFormat)} ================",
        "------------------ Current Traffic ------------------",
        "Bytes Transferred: $dataTransferred",
        "Successful Requests: $successfulRequests",
        "Redirects: $redirectRequests",
        "Client Errors: $clientErrorRequests",
        "Server Errors: $serverErrorsRequests",

        if (topRequestsBySection.isNotEmpty()) {
          listOf(
            "Requests By Sections:",
            topRequestsBySection.joinToString("\n") { entry ->
              "\t[${entry.value}] ${entry.key}"
            }
          ).joinToString("\n")
        } else "",
        "----------------------- Total -----------------------",
        "Total Requests: $totalRequests",
        "Total Data Transferred: $totalDataTransferred"

      ).forEach(::println)
    }
    repeat(2) { println() }
  }

  /**
   * Doesn't fully clear the screen but resets the cursor,
   * so you can still scroll through past events.
   */
  private fun clearScreen() {
    print("\u001B[H\u001B[2J");
  }
}