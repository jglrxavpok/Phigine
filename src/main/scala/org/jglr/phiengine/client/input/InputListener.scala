package org.jglr.phiengine.client.input

trait InputListener {
  def onKeyPressed(keycode: Int): Boolean

  def onKeyReleased(keycode: Int): Boolean

  def onKeyTyped(character: Char): Boolean

  def onMousePressed(screenX: Int, screenY: Int, button: Int): Boolean

  def onMouseReleased(screenX: Int, screenY: Int, button: Int): Boolean

  def onMouseMoved(screenX: Int, screenY: Int): Boolean

  def onScroll(dir: Int): Boolean
}
