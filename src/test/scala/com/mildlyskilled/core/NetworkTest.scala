package com.mildlyskilled.core

import org.scalatest.FunSuite

class NetworkTest extends FunSuite {

  test("interfaces"){
    assert(Network.interfaces.nonEmpty)
  }

  test("ipAddresses") {
    assert(Network.ipAddresses.nonEmpty)
    assert(Network.ipAddresses.isInstanceOf[List[String]])
  }

  test("addressMap") {
    assert(Network.addressMap.nonEmpty)
    assert(Network.addressMap.isInstanceOf[Map[String, String]])
  }
}
