package salve.tests

import akka.actor.{Props, ActorRef, Actor}
import org.scalatest._
import org.scalatest.concurrent.AsyncAssertions._
import salve.main.Salve
import salve.combatlog.LogEvent
import org.scalatest.time.SpanSugar._

class TickActor(w: Waiter) extends Actor with Matchers {
  def receive = {
    case _ => w.dismiss()
  }
}

class TickTest extends UnitSpec {
  val testReplay = getClass.getResource("/replay.dem")
  val actorWaiter = new Waiter
  val callbackWaiter = new Waiter


  "#combatLog" should "dispatch entries to the callback once started if an actor is passed" in {
    val tickActor = Props(new TickActor(actorWaiter))
    val salve = new Salve(testReplay.getPath())

    salve everyTick tickActor

    salve start

    actorWaiter await timeout(10 seconds)

    salve stop
  }

  "#combatLog" should "dispatch entries to the callback once started if a block is passed" in {
    val salve = new Salve(testReplay.getPath())

    salve everyTick {
      callbackWaiter dismiss
    }

    salve start

    callbackWaiter await timeout(10 seconds)

    salve stop
  }

}
