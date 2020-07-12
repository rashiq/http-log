package monitor

import EventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Configuration
import model.LogEvent
import model.LogLine
import model.RenderUiEvent
import ticker

class StatisticsMonitor(
  private val configuration: Configuration,
  private val bus: EventBus
) : Monitor {

  private val statistics = Statistics()

  override fun monitor() = GlobalScope.launch(Dispatchers.Default) {
    launch {
      // refresh the screen every 10 seconds
      ticker(10) {
        publishLogs()
      }
    }

    for (event in bus.subscribe<LogEvent>()) {
      processLogs(event)
    }
  }

  private fun processLogs(event: LogEvent) {
    statistics.add(event.log)
  }

  private fun publishLogs() {
    synchronized(statistics) {
      with(statistics) {
        bus.dispatch(
          RenderUiEvent(
            totalRequests,
            totalDataTransferred,
            dataTransferred,
            topRequestsBySection(),
            successfulRequests,
            redirectRequests,
            clientErrorRequests,
            serverErrorsRequests
          )
        )
      }
      statistics.clear()
    }
  }
}


class Statistics {
  var totalRequests: Int = 0
  var totalDataTransferred: Int = 0
  var dataTransferred = 0
  var successfulRequests = 0
  var redirectRequests = 0
  var clientErrorRequests = 0
  var serverErrorsRequests = 0

  private val requestsBySection = HashMap<String, Int>()

  fun topRequestsBySection(): List<Map.Entry<String, Int>> {
    val getVal: (String) -> Int = { x -> requestsBySection.getOrDefault(x, 0) }
    return requestsBySection.toSortedMap(Comparator { a, b ->
      val compare = getVal(b).compareTo(getVal(a))
      if (compare == 0) 1 else compare
    }).entries.take(5)
  }

  @Synchronized fun add(log: LogLine) {
    with(log) {
      totalRequests += 1
      totalDataTransferred += size

      dataTransferred += size
      requestsBySection[section] = requestsBySection[section]?.plus(1) ?: 1
      when {
        status.startsWith("2") -> successfulRequests += 1
        status.startsWith("3") -> redirectRequests += 1
        status.startsWith("4") -> clientErrorRequests += 1
        status.startsWith("5") -> serverErrorsRequests += 1
      }
    }
  }

  @Synchronized fun clear() {
    dataTransferred = 0
    successfulRequests = 0
    redirectRequests = 0
    clientErrorRequests = 0
    serverErrorsRequests = 0
    requestsBySection.clear()
  }
}