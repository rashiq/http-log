import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import model.Configuration
import monitor.AlertsMonitor
import monitor.LogMonitor
import monitor.StatisticsMonitor
import sun.misc.Signal
import ui.Terminal
import java.io.File
import kotlin.system.exitProcess


class Application : CliktCommand() {
  private val logFile: String by option(help = "Log file to monitor").default("/tmp/access.log")
  private val alertThreshold: Int by option(help = "Alert threshold for requests per second").int().default(10)
  private val alertWindow: Int by option(help = "Alert window for threshold").int().default(2)


  override fun run() {
    val file = File(logFile)
    if (!file.exists() || !file.isFile) {
      println("Log file $logFile doesn't exist")
      return
    }

    val configuration = Configuration(
      logFile = file,
      alertingThreshold = alertThreshold,
      alertingWindow = alertWindow
    )

    val bus = EventBus()
    val logMonitor = LogMonitor(configuration, LogParser(), bus)
    val alertsMonitor = AlertsMonitor(configuration, bus)
    val statisticsMonitor = StatisticsMonitor(configuration, bus)

    val monitors: List<Job> = listOf(
      logMonitor,
      alertsMonitor,
      statisticsMonitor
    ).map { it.monitor() }

    Signal.handle(Signal("INT")) {
      monitors.forEach { it.cancel() }
      bus.close()
      exitProcess(0)
    }

    runBlocking {
      Terminal(bus).render()
    }
  }
}

fun main(args: Array<String>) = Application().main(args)