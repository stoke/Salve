package salve.combatlog

import skadistats.clarity.`match`.Match
import skadistats.clarity.model.{StringTable, GameEventDescriptor, GameEvent}
import scala.collection.JavaConversions._
import skadistats.clarity.Clarity
import skadistats.clarity.parser.Profile
import java.lang.reflect.Field

case class LogEvent(id: Int, entry: LogEntry)

object LogType {
  val Hit = 0
  val Heal = 1
  val Buff = 2
  val Debuff = 3
  val Kill = 4
  val Skill = 5
  val Use = 6
  val Location = 7
  val Gold = 8
  val Pause = 9
  val XP = 10
  val Buy = 11
  val BuyBack = 12
}

class LogEntry(event: GameEvent, descriptor: GameEventDescriptor, names: StringTable) {
  if (event.getEventId() != descriptor.getEventId())
    throw new Error("EventIds have to match")

  val originalDescriptor = event.getClass().getDeclaredField("descriptor")
  val idx = descriptor.getIndexForKey("type")

  originalDescriptor.setAccessible(true)
  originalDescriptor.set(event, descriptor)
  originalDescriptor.setAccessible(false)

  val id: Int = event.getProperty("type")

  val attackerIdx: Int = event.getProperty("attackername")
  val targetIdx: Int = event.getProperty("targetname")
  val inflictorIdx: Int = event.getProperty("inflictorname")
  val targetSourceIdx: Int = event.getProperty("targetsourcename")

  val attacker = names.getNameByIndex(attackerIdx)
  val target = names.getNameByIndex(targetIdx)
  val inflictor = names.getNameByIndex(inflictorIdx)
  val targetSource = names.getNameByIndex(targetSourceIdx)

  val attackerIllusion: Boolean = event.getProperty("attackerillusion")
  val targetIllusion: Boolean = event.getProperty("targetillusion")

  val value: Int = event.getProperty("value")
  val health: Int = event.getProperty("health")
  val timestamp: Float = event.getProperty("timestamp")
}