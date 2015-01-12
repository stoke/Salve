package salve.main

import salve.resource.PlayerResource

import scala.collection.Seq
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.collection.JavaConversions._

import akka.actor._
import akka.event.Logging

import salve.combatlog.LogEvent
import salve.actors.{ResourceCallback, MainActor, LogCallback, Start}



class Salve(filename: String) {
  private val system = ActorSystem("SalveSystem" + System.currentTimeMillis)
  private val main = Props(new MainActor(filename))
  private val mainActor = system.actorOf(main, "mainactor")

  var resources: Seq[PlayerResource] = Seq()

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

  def playerResources(f: (Seq[PlayerResource]) => Unit) = {
    mainActor ! ResourceCallback(f)
  }

  def playerResources(actor: Props) = {
    val instancedResourceActor = system.actorOf(actor, "resourceactor" + System.currentTimeMillis)

    mainActor ! ResourceCallback(instancedResourceActor)
  }

  def stop = {
    system.actorSelection("*") ! PoisonPill
  }

}
