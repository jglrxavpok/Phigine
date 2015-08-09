package org.jglr.phiengine.client.ui.layouts

import java.util
import java.util.{LinkedList, List}
import scala.collection.JavaConversions._
import org.jglr.phiengine.core.utils.JavaConversions._

import org.jglr.phiengine.client.ui.{UILayout, UIComponent}
import org.jglr.phiengine.core.maths.Vec2

object FlowLayout extends Enumeration {
  type Type = Value
  val CENTER, LEFT, RIGHT = Value
}

class FlowLayout(comp: UIComponent, var xSpacing: Float = 5f, var ySpacing: Float = 5f, layoutType: FlowLayout.Type = FlowLayout.LEFT) extends UILayout(comp) {

  override def onComponentAdded(added: UIComponent): Unit = {
    super.onComponentAdded(added)
    recalculatePositions()
  }

  override def onComponentRemoved(removed: UIComponent): Unit = {
    super.onComponentRemoved(removed)
    recalculatePositions()
  }

  override def pack(): Vec2 = {
    recalculatePositions()
    super.pack()
  }

  def move(rowList: List[UIComponent], xCentered: Boolean, startX: Float, startY: Float, widthReduction: Float, direction: Int, currentWidth: Float): Float = {
    if(rowList.isEmpty)
      return 0f
    var currentX = startX
    val maxH: Float = rowList.map((c: UIComponent) => c.h).max
    if(xCentered) {
      var x: Float = startX-currentWidth/2f
      for(child <- rowList) {
        child.x = x + comp.x
        child.y = startY+comp.y
        child.onMoved()
        x += direction * (child.w + xSpacing)
      }
    } else {
      for(child <- rowList) {
        child.x = currentX + comp.x - widthReduction * child.w
        child.y = startY+comp.y
        child.onMoved()
        currentX += direction * (child.w + xSpacing)
      }
    }
    maxH
  }

  override def recalculatePositions(): Unit = {
    var startX: Float = 0f
    var widthReduction: Float = 0f
    var xCentered: Boolean = false
    var direction = 1
    layoutType match {
      case FlowLayout.CENTER =>
        startX = comp.w/2f
        xCentered = true
        widthReduction = 0.5f

      case FlowLayout.LEFT =>
        startX = 0
        xCentered = false
        widthReduction = 0

      case FlowLayout.RIGHT =>
        startX = comp.w
        xCentered = false
        widthReduction = 1f
        direction = -1

      case _ =>
    }

    var currentY = comp.margins.y
    var currentWidth = 0f
    val rowList = new LinkedList[UIComponent]()
    for(c <- comp.children) {
      if(currentWidth + c.w + xSpacing > comp.w+comp.margins.x*2) {
        currentY += move(rowList, xCentered, startX+comp.margins.x, currentY, widthReduction, direction, currentWidth)+ySpacing
        currentWidth = 0f
        rowList.clear()
      }
      rowList.add(c)
      currentWidth+=c.w+xSpacing
    }
    if(!rowList.isEmpty)
      currentY += move(rowList, xCentered, startX+comp.margins.x, currentY, widthReduction, direction, currentWidth)+ySpacing
    rowList.clear()
  }
}
