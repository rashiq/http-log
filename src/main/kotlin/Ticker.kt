import kotlinx.coroutines.*

suspend fun ticker(seconds: Long = 1, invoke: () -> Unit) {
  do {
    invoke()
    delay(seconds * 1000)
  } while (true)
}
