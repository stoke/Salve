package salve.tests

import akka.actor.{Props, ActorRef, Actor}
import org.scalatest._
import org.scalatest.concurrent.AsyncAssertions._
import salve.main.Salve
import salve.combatlog.LogEvent
import org.scalatest.time.SpanSugar._

abstract class UnitSpec extends FlatSpec with Matchers

class LogActor(w: Waiter) extends Actor with Matchers {
  def receive = {
    case LogEvent(id, entry) => {
      w {
        entry.value shouldBe an [Integer]
      }

      w.dismiss()
    }

    case _ => {}
  }
}

class LogTest extends UnitSpec {
  val testReplay = getClass.getResource("/replay.dem")
  val actorWaiter = new Waiter
  val callbackWaiter = new Waiter


  "#combatLog" should "dispatch entries to the callback once started if an actor is passed" in {
    val logActor = Props(new LogActor(actorWaiter))
    val salve = new Salve(testReplay.getPath())

    salve combatLog logActor

    salve start

    actorWaiter await timeout(10 seconds)

    salve stop
  }

  "#combatLog" should "dispatch entries to the callback once started if a block is passed" in {
    val salve = new Salve(testReplay.getPath())

    salve combatLog { event: LogEvent =>
      val entry = event.entry

      callbackWaiter {
        entry.value shouldBe an [Integer]
      }

      callbackWaiter dismiss
    }

    salve start

    callbackWaiter await timeout(10 seconds)

    salve stop
  }

}
