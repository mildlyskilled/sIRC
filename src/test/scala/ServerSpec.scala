import akka.testkit.TestFSMRef
import com.mildlyskilled.actors.Server
import com.mildlyskilled.protocol.Server._

class ServerSpec extends ActorHarness {

  "The Server" must {

    val server = TestFSMRef(new Server)

    "Must be idle when instantiated" in {
      server.underlyingActor.stateName must be(Idle)
    }

    "Must transition to active when it receives a Start message" in {
      server ! Start
      server.underlyingActor.stateName must be(Active)
      server.underlyingActor.stateData must be(Channels(Nil))
    }

    "Must allow new channels to be created" in {
      server ! Register(testActor)
      server.underlyingActor.stateData must be (Channels(List(testActor)))
    }
  }
}
