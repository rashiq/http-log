import kotlinx.coroutines.*

/**
 * The [ticker] function can execute the specified [invoke] function in
 * intervals for the specified [seconds].
 *
 * It will suspend the coroutine it's being run on and will run indefinitely
 * so make sure to not run it in its own coroutine, not using the [GlobalScope],
 * otherwise it will run forever.
 *
 * ```
 * launch {
 *   // every 10 seconds
 *   ticker(10) {
 *     // do stuff
 *   }
 * }
 * ```
 */
suspend fun ticker(seconds: Long = 1, invoke: () -> Unit) = withContext(Dispatchers.Default) {
  do {
    invoke()
    delay(seconds * 1000)
  } while (true)
}
