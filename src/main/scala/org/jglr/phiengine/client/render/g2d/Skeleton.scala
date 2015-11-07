package org.jglr.phiengine.client.render.g2d

import java.util

import com.google.common.base.Preconditions._
import org.jglr.phiengine.core.maths.Vec2

import scala.collection.JavaConversions._

class Skeleton extends SkeletonPart(null) {
  val position = new Vec2(0f, 0f)

  override def update(delta: Float): Unit = {
    xPos = position.x
    yPos = position.y
    super.update(delta)
  }
}

class SkeletonPart(val parent: SkeletonPart) {

  private val children: util.List[SkeletonPart] = new util.ArrayList[SkeletonPart]
  var sprite: Sprite = null
  var angle = 0f
  val anchor = new Vec2(0f,0f)
  var xPos = 0f
  var yPos = 0f
  var renderAngle = 0f
  val spriteOffset = new Vec2(0f,0f)
  val transAnchor: Vec2 = new Vec2(0f,0f)
  var renderable = true

  def getChildCount: Int = children.size

  def createChild: SkeletonPart = {
    createChild(0,0)
  }

  def createChild(anchorX: Float, anchorY: Float): SkeletonPart = {
    val child = new SkeletonPart(this)
    child.anchor(anchorX, anchorY)
    addChild(child)
    child
  }

  def addChild(part: SkeletonPart): Unit = {
    checkNotNull(part, "part", Array())
    children.add(part)
  }

  def removeChild(part: SkeletonPart): Unit = {
    checkNotNull(part, "part", Array())
    children.remove(part)
  }

  def update(delta: Float): Unit = {
    renderAngle = angle
    transAnchor(anchor)
    if(parent != null) {
      val parentAngle = parent.renderAngle
      renderAngle += parentAngle
      val dist = parent.anchor - anchor
      transAnchor(dist.rotate(parentAngle) += parent.transAnchor)
      xPos = -transAnchor.x + parent.xPos
      yPos = -transAnchor.y + parent.yPos
    }
    for(c <- children) {
      c.update(delta)
    }
  }

  def render(delta: Float, spriteBatch: SpriteBatch): Unit = {
    if(!renderable)
      return
    if(sprite != null) {
      var offset = spriteOffset
      if(parent != null) {
        offset = spriteOffset + parent.spriteOffset
      }
      sprite.setRotationCenter(-spriteOffset.x, -spriteOffset.y)
      sprite.setPosition(xPos+offset.x, yPos+offset.y)
      sprite.setRotation(renderAngle)
      sprite.draw(spriteBatch)
    }
    for(c <- children) {
      c.render(delta, spriteBatch)
    }
  }
}

