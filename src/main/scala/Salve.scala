package salve.main

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.collection.JavaConversions._

import akka.actor._
import akka.event.Logging

import salve.combatlog.LogEvent
import salve.actors.{MainActor, LogCallback, Start}

class Salve(filename: String) {
  val system = ActorSystem("SalveSystem" + System.currentTimeMillis)
  val main = Props(new MainActor(filename))
  val mainActor = system.actorOf(main, "mainactor")

  def start = {
    mainActor ! Start(this)
  }

  def combatLog(f: (LogEvent) => Unit) = {
    mainActor ! LogCallback(f)
  }

  def combatLog(actor: Props) = {
    val instancedLogActor = system.actorOf(actor, "logactor" + System.currentTimeMillis)

    mainActor ! LogCallback(instancedLogActor)
  }

  def stop = {
    system.actorSelection("*") ! PoisonPill
  }

}
