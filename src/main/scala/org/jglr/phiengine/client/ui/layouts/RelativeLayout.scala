package org.jglr.phiengine.client.ui.layouts

import org.jglr.phiengine.client.ui.{UIComponent, UILayout}

import scala.collection.JavaConversions._

class RelativeLayout(comp: UIComponent) extends UILayout(comp) {

  override def onComponentAdded(added: UIComponent): Unit = {
    super.onComponentAdded(added)
    added.x += comp.x
    added.y += comp.y
  }

  override def onComponentRemoved(removed: UIComponent): Unit = {
    super.onComponentRemoved(removed)
  }

  override def recalculatePositions(): Unit = {
    for(c <- comp.children) {
      c.x = getPosition(c).x+comp.x
      c.y = getPosition(c).y+comp.y
      c.onMoved()
    }
  }
}
