import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import model.Event

/**
 * A simple implementation of an Eventbus using a [ConflatedBroadcastChannel],
 * that allows multiple consumers and multiple senders.
 *
 * You can create an instance of an [EventBus] listening to a specific event with
 * ```
 * val bus = EventBus()
 * for (event in bus.subscribe<Event>()) {}
 * ```
 * and send an event using the [dispatch] method.
 */
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