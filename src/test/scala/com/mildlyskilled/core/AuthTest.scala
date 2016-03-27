package com.mildlyskilled.core

import java.util.logging.Logger

import akka.actor.{ActorSystem, Props}
import com.mildlyskilled.actors.Client
import org.scalatest.{BeforeAndAfterAll, FunSuite}

/**
  * Created by kwabena on 22/03/2016.
  */
class AuthTest extends FunSuite with BeforeAndAfterAll {

  val system = ActorSystem("fakesys")
  val logger = Logger.getLogger("testlogger")

  override def afterAll() {
    system.terminate()
    logger.info("System brought down")
  }

  test("testIsAuthenticated") {
    val auth = new Auth
    val testActor = system.actorOf(Props[Client], "tc")
    assert(auth.isAuthenticated(testActor))
  }

  test("testLogin") {
    val auth = new Auth
    assert(auth.login("test", "test"))
  }

}
