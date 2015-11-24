package org.jglr.phiengine.network

object NetworkSide extends Enumeration {
  case class NetworkSideVal(id: Int, client: Boolean) extends Value {
    def isClient: Boolean = {
      client
    }

    def isServer: Boolean = {
      !client
    }
  }

  type NetworkSide = NetworkSideVal
  val CLIENT = NetworkSideVal(0, true)
  val SERVER = NetworkSideVal(0, false)

  override val values = Array(CLIENT, SERVER)
}
