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

class LogDispatcherActor extends Dispatcher[LogEntry, LogEvent] {
  def entryFormat(entry: LogEntry): LogEvent = {
    LogEvent(entry.id, entry)
  }
}

