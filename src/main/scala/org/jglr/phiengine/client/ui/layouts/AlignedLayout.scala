package org.jglr.phiengine.client.ui.layouts

import org.jglr.phiengine.client.ui.{UILayout, UIComponent}
import org.joml.Vector2f
import scala.collection.JavaConversions._

object AlignDirection extends Enumeration {

  type Type = Value

  val LeftToRight, RightToLeft, TopToBottom, BottomToTop = Value
}

class AlignedLayout(comp: UIComponent, direction: AlignDirection.Type = AlignDirection.LeftToRight, spacing: Float = 5f) extends UILayout(comp) {

  override def onComponentAdded(added: UIComponent): Unit = {
    super.onComponentAdded(added)
    recalculatePositions()
  }

  override def onComponentRemoved(removed: UIComponent): Unit = {
    super.onComponentRemoved(removed)
    recalculatePositions()
  }

  override def recalculatePositions(): Unit = {
    val ownerX = comp.x
    val ownerY = comp.y
    val ownerW = comp.w
    val ownerH = comp.h
    var fixedCoordinate = 0f
    var factor = 1f
    var varyingX = false

    direction match {
      case AlignDirection.LeftToRight =>
        fixedCoordinate = ownerY+ownerH/2f
        varyingX = true

      case AlignDirection.RightToLeft =>
        fixedCoordinate = ownerY+ownerH/2f
        varyingX = true
        factor = -1f

      case AlignDirection.TopToBottom =>
        fixedCoordinate = ownerX+ownerW/2f

      case AlignDirection.BottomToTop =>
        fixedCoordinate = ownerX+ownerW/2f
        factor = -1f
    }

    var varying = 0f

    for(c <- comp.children) {
      val x = if(varyingX) varying else fixedCoordinate
      val y = if(!varyingX) varying else fixedCoordinate
      val size = if(varyingX) c.w else c.h

      c.x = x + ownerX
      c.y = y + ownerY

      if(!varyingX) {
        c.x -= c.w/2f
      } else {
        c.y -= c.h/2f
      }

      if(c.layout != null)
        c.layout.recalculatePositions()

      varying += (spacing + size)*factor
    }
  }

  override def pack(): Vector2f = {
    recalculatePositions()
    super.pack()
  }
}
