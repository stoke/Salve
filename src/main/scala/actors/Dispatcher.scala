package salve.actors

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import akka.actor.{ActorRef, Actor}
import salve.combatlog.{LogEntry, LogEvent}

// T: LogEntry
// A: LogEvent

abstract class Dispatcher[T : ClassTag, A : ClassTag] extends Actor {
  var callbacks = new ArrayBuffer[(A) => _]
  var actors = new ArrayBuffer[ActorRef]

  def receive = {
    case entry: T => { dispatch(entry) }
    case f: (A => Any) => { add(f) }
    case ar: ActorRef => { add(ar) }
    case _ => None
  }

  def entryFormat(entry: T): A

  def dispatch(entry: T) = {
    val formatted = entryFormat(entry)

    callbacks foreach { callback =>
      callback(formatted)
    }

    actors foreach { actor: ActorRef =>
      actor ! formatted
    }
  }

  def add(f: (A) => Any) = {
    callbacks += f
  }

  def add(actor: ActorRef) = {
    actors += actor
  }
}
