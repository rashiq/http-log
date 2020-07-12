import kotlinx.coroutines.*
import model.LogLine
import java.time.ZonedDateTime

fun generateLogEvents(n: Int = 1): MutableList<LogLine> {
  val list = ArrayList<LogLine>()
  repeat(n) {
    list.add(
      LogLine(
        "127.0.0.1",
        "rashiq",
        ZonedDateTime.now(),
        "DELETE",
        "/bad/code",
        "/bad/",
        "200",
        256
      )
    )
  }
  return list
}