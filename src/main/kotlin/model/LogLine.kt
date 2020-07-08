package model

import java.time.ZonedDateTime

data class LogLine(
  val client: String,
  val userId: String,
  val dateTime: ZonedDateTime?,
  val method: String,
  val request: String,
  val section: String,
  val status: String,
  val size: Int
)