package org.jglr.phiengine.network

object NetworkSide extends Enumeration {

  case class NetworkSideVal(val id: Int, client: Boolean) extends Value {
    def isClient: Boolean = {
      client
    }

    def isServer: Boolean = {
      !client
    }
  }

  type NetworkSide = NetworkSideVal
  val CLIENT = NetworkSideVal(0, true)
  val SERVER = NetworkSideVal(1, false)

  def get(byte: Byte): NetworkSide = {
    if(byte == 0)
      CLIENT
    else
      SERVER
  }

}
