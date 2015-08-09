package org.jglr.phiengine.client.ui.layouts

import org.jglr.phiengine.client.ui.{UIComponent, UILayout}

import scala.collection.JavaConversions._

class RelativeLayout(comp: UIComponent) extends UILayout(comp) {

  override def onComponentAdded(added: UIComponent): Unit = {
    super.onComponentAdded(added)
    added.x += comp.x+comp.margins.x
    added.y += comp.y+comp.margins.y
  }

  override def onComponentRemoved(removed: UIComponent): Unit = {
    super.onComponentRemoved(removed)
  }

  override def recalculatePositions(): Unit = {
    for(c <- comp.children) {
      c.x = getPosition(c).x+comp.x+comp.margins.x
      c.y = getPosition(c).y+comp.y+comp.margins.y
      c.onMoved()
    }
  }
}
