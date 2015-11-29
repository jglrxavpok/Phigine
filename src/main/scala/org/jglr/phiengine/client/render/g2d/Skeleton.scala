package org.jglr.phiengine.client.render.g2d

import java.util

import com.google.common.base.Preconditions._
import org.joml.Vector2f

import scala.collection.JavaConversions._
import org.jglr.phiengine.core.maths.VectorfExtensions._

class Skeleton extends SkeletonPart(null) {
  val position = new Vector2f(0f, 0f)

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
  val anchor = new Vector2f(0f,0f)
  var xPos = 0f
  var yPos = 0f
  var renderAngle = 0f
  val spriteOffset = new Vector2f(0f,0f)
  val transAnchor: Vector2f = new Vector2f(0f,0f)
  var renderable = true

  def getChildCount: Int = children.size

  def createChild: SkeletonPart = {
    createChild(0,0)
  }

  def createChild(anchorX: Float, anchorY: Float): SkeletonPart = {
    val child = new SkeletonPart(this)
    child.anchor.set(anchorX, anchorY)
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
    transAnchor.set(anchor)
    if(parent != null) {
      val parentAngle = parent.renderAngle
      renderAngle += parentAngle
      val dist = parent.anchor.sub(anchor)
      transAnchor.set(dist.rotate(parentAngle).add(parent.transAnchor))
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
      val offset = spriteOffset.copy()
      if(parent != null) {
        Vector2f.add(spriteOffset, parent.spriteOffset, offset)
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

