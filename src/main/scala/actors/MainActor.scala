package salve.actors

import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import akka.event.Logging
import salve.main.Salve
import salve.resource.PlayerResource

import scala.Some

import skadistats.clarity.`match`.Match
import skadistats.clarity.model.{Entity, GameEventDescriptor, GameEvent}
import salve.combatlog.LogEntry
import scala.collection.JavaConversions._
import skadistats.clarity.Clarity
import skadistats.clarity.parser.Profile
import skadistats.clarity.parser.TickIterator

case class Start(instance: Salve)

class MainActor(filename: String) extends Actor with ResourcesTrait {
  val iter = Clarity.tickIteratorForFile(filename, Profile.COMBAT_LOG, Profile.ENTITIES)
  val m = new Match
  var logDescriptor: Option[GameEventDescriptor] = None

  val logDispatcher = context.actorOf(Props[LogDispatcherActor], "logdispatcher")
  val resourceDispatcher = context.actorOf(Props[ResourceDispatcherActor])
  val tickDispatcher = context.actorOf(Props[TickDispatcherActor])

  def receive = {
    case Start(instance) => start(instance)
    case LogCallback(f) => logDispatcher ! f
    case ResourceCallback(f) => resourceDispatcher ! f
    case TickCallback(f) => tickDispatcher ! f
    case _ => {}
  }

  def start(salve: Salve) = {
    iter foreach { tick =>
      tick.apply(m)

      tickDispatcher ! tick

      if (logDescriptor.isEmpty)
        logDescriptor = Some(m.getGameEventDescriptors.forName("dota_combatlog"))

      val stringTable = m.getStringTables forName "CombatLogNames"

      m.getGameEvents() foreach { event: GameEvent =>
        val descriptor = logDescriptor.get

        if (event.getEventId() == descriptor.getEventId()) {
          val entry = new LogEntry(event, descriptor, stringTable)

          logDispatcher ! entry
        }
      }

      m.getEntities.getAllByDtName("DT_DOTA_PlayerResource") foreach { entity: Entity =>
        resourceDispatcher ! entity

        salve.resources = resourcesParse(entity)
      }
    }
  }
}
