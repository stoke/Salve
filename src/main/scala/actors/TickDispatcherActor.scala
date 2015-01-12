package salve.actors

import skadistats.clarity.parser.Tick

case class TickCallback[T](f: T)

class TickDispatcherActor extends Dispatcher[Tick, Unit] {
  def entryFormat(tick: Tick) = {}
}
