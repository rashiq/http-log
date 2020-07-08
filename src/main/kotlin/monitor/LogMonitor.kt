package monitor

import EventBus
import LogParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Configuration
import model.LogErrorEvent
import model.LogEvent
import org.apache.commons.io.input.Tailer
import org.apache.commons.io.input.TailerListenerAdapter
import java.io.FileNotFoundException

class LogMonitor(
  private val configuration: Configuration,
  private val logParser: LogParser,
  private val bus: EventBus
) : Monitor {

  private val callback = object : TailerListenerAdapter() {
    override fun handle(line: String?) {
      val logLine = line?.let { logParser.parse(it) }
      logLine?.let {
        bus.dispatch(LogEvent(logLine))
      }
    }

    override fun handle(exception: Exception?) {
      exception?.let { bus.dispatch(LogErrorEvent(exception)) }
    }

    override fun fileNotFound() {
      bus.dispatch(LogErrorEvent(FileNotFoundException()))
    }
  }

  private val tailer = Tailer(configuration.logFile, callback, 1000, true)

  override fun monitor() = GlobalScope.launch(Dispatchers.Default) {
    tailer.run()
  }

  fun stop() {
    tailer.stop()
  }
}