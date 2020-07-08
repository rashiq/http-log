import model.LogLine
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class LogParser {

  companion object {
    val REGEX = """
      ^(?<client>\S+) \S+ (?<userId>\S+) \[(?<dateTime>[^\]]+)\] "(?<method>[A-Z]+) (?<request>[^ "]+)? HTTP\/[0-9.]+" (?<status>[0-9]{3}) (?<size>[0-9]+|-)
    """.trim().toRegex()
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z")

  }

  fun parse(log: String): LogLine? {
    return REGEX.find(log)?.groups?.let {
      LogLine(
        client = it["client"]?.value ?: "",
        userId = it["userId"]?.value ?: "",
        dateTime = ZonedDateTime.parse(it["dateTime"]?.value, DATE_TIME_FORMATTER),
        method = it["method"]?.value ?: "",
        request = it["request"]?.value ?: "",
        status = it["status"]?.value ?: "",
        size = it["size"]?.value?.toInt() ?: 0,
        section = parseSection(it["request"]?.value)
      )
    }
  }
}

fun parseSection(request: String?): String {
  val section = request?.split("/")?.firstOrNull(String::isNotEmpty)
  return section?.let { "/$section/" } ?: "/"
}