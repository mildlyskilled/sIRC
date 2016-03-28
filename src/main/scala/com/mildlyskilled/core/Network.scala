package com.mildlyskilled.core

import java.net.{NetworkInterface, InetAddress}
import scala.collection.JavaConversions._

object Network {

  def distillIpAddresses(interface: NetworkInterface): String = {
    val interfaceOption = interface.getInterfaceAddresses.find(_.getBroadcast != null)
    interfaceOption match {
      case Some(iFace) => iFace.getAddress.getHostAddress
      case None => "Invalid IP"
    }
  }

  val interfaces = NetworkInterface.getNetworkInterfaces.toList.filter(!_.isLoopback).filter(_.isUp)

  val ipAddresses = interfaces.map((x) => distillIpAddresses(x)).filterNot((address) => address == "Invalid IP")

  val addressMap = (ipAddresses.indices.map(_ + 1).map(_.toString) zip ipAddresses).toMap

}
