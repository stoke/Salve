package salve.tests

import akka.actor.{Props, ActorRef, Actor}
import org.scalatest._
import org.scalatest.concurrent.AsyncAssertions._
import salve.main.Salve
import salve.combatlog.LogEvent
import org.scalatest.time.SpanSugar._
import salve.resource.PlayerResource

class ResourceActor(w: Waiter) extends Actor with Matchers {
  def receive = {
    case resources: Seq[PlayerResource] => {
      w {
        resources(1).playerName shouldBe a [String]
      }

      w.dismiss()
    }

    case _ => {}
  }
}

class ResourceTest extends UnitSpec {
  val testReplay = getClass.getResource("/replay.dem")
  val actorWaiter = new Waiter
  val callbackWaiter = new Waiter

  "#playerResources" should "dispatch entries to the callback once started if an actor is passed" in {
    val resourceActor = Props(new ResourceActor(actorWaiter))
    val salve = new Salve(testReplay.getPath())

    salve playerResources resourceActor

    salve start

    actorWaiter await timeout(10 seconds)

    salve stop
  }

  "#playerResources" should "dispatch entries to the callback once started if a block is passed" in {
    val salve = new Salve(testReplay.getPath())

    salve playerResources { resources: Seq[PlayerResource] =>
      callbackWaiter {
        resources(1).playerName shouldBe a [String]
      }

      callbackWaiter dismiss
    }

    salve start

    callbackWaiter await timeout(10 seconds)

    salve stop
  }

}
