package com.mildlyskilled.core

import akka.actor.ActorRef

class Auth {

  def login(username: String, password: String): Boolean = {
    true
  }

  def isAuthenticated(user: ActorRef): Boolean = true
}
