import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun ticker(seconds: Long = 1, invoke: () -> Unit) = GlobalScope.launch(Dispatchers.Default) {
  do {
    invoke()
    delay(seconds * 1000)
  } while (true)
}