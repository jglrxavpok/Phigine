package org.jglr.phiengine.client.ui

abstract class UILayout(comp: UIComponent) {
  def onComponentAdded(added: UIComponent): Unit = {}

  def onComponentRemoved(removed: UIComponent): Unit = {}

  def pack(): (Float, Float)

  def recalculatePositions(): Unit
}
