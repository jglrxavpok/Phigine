package org.jglr.phiengine.client.input

import java.util.List

trait ControllerListener {
  def onConnection(controller: Controller)

  def onDisconnection(controller: Controller)

  def onButtonPressed(controller: Controller, buttonCode: Int): Boolean

  def onButtonReleased(controller: Controller, buttonCode: Int): Boolean

  def onAxisMoved(controller: Controller, axisCode: Int, value: Float): Boolean

  def onPovMoved(controller: Controller, povCode: Int, value: PovDirection.Type): Boolean
}