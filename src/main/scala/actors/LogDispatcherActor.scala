package salve.actors

import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import akka.event.Logging

import skadistats.clarity.`match`.Match
import skadistats.clarity.model.{GameEventDescriptor, GameEvent}
import salve.combatlog.{LogEntry, LogEvent}
import scala.collection.JavaConversions._
import skadistats.clarity.Clarity
import skadistats.clarity.parser.Profile
import skadistats.clarity.parser.TickIterator

import scala.collection.mutable.ArrayBuffer

case class LogCallback[T](f: T)

class LogDispatcherActor() extends Actor {
  var callbacks = new ArrayBuffer[(LogEvent) => Unit]
  var actors = new ArrayBuffer[ActorRef]

  def receive = {
    case entry: LogEntry => { dispatch(entry) }
    case f: ((LogEvent) => Unit) => { add(f) }
    case ar: ActorRef => { add(ar) }
    case _ => {}
  }

  def dispatch(entry: LogEntry) = {
    callbacks foreach { callback =>
      val event = LogEvent(entry.id, entry)

      callback(event)
    }

    actors foreach { actor: ActorRef =>
      actor ! LogEvent(entry.id, entry)
    }
  }

  def add(f: (LogEvent) => Unit) = {
    callbacks += f
  }

  def add(actor: ActorRef) = {
    actors += actor
  }
}

