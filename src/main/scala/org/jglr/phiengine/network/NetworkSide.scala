package org.jglr.phiengine.network

object NetworkSide extends Enumeration {

  type NetworkSide = Value
  val CLIENT, SERVER = Value

  def get(byte: Byte): NetworkSide = {
    for(v <- values) {
      if(byte == v.id.toByte) {
        return v
      }
    }
    null
  }


}
