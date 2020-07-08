import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import model.Event

class EventBus {
  val bus: BroadcastChannel<Event> = ConflatedBroadcastChannel()

  fun dispatch(event: Event) {
    GlobalScope.launch(Dispatchers.Default) {
      bus.send(event)
    }
  }

  inline fun <reified T> subscribe(): ReceiveChannel<T> where T : Event {
    return bus.openSubscription().filter { it is T }.map { it as T }
  }

  fun close() {
    bus.close()
  }
}