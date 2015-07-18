package org.jglr.phiengine.core.maths

class AABB(var x: Float, var y: Float, var w: Float, var h: Float) {

  def this(w: Float, h: Float) {
    this(0, 0, w, h)
  }

  def translate(x: Float, y: Float): AABB = {
    new AABB(this.x + x, this.y + y, w, h)
  }

  def collides(o: AABB): Boolean = {
    val minX: Float = Math.min(x, x + w)
    val maxX: Float = Math.max(x, x + w)
    val minY: Float = Math.min(y, y + h)
    val maxY: Float = Math.max(y, y + h)
    val ominX: Float = Math.min(o.x, o.x + o.w)
    val omaxX: Float = Math.max(o.x, o.x + o.w)
    val ominY: Float = Math.min(o.y, o.y + o.h)
    val omaxY: Float = Math.max(o.y, o.y + o.h)
    !(omaxX < minX || ominX > maxX || omaxY < minY || ominY > maxY)
  }

  def getX: Float = {
    x
  }

  def setX(x: Float) {
    this.x = x
  }

  def getY: Float = {
    y
  }

  def setY(y: Float) {
    this.y = y
  }

  def getW: Float = {
    w
  }

  def setW(w: Float) {
    this.w = w
  }

  def getH: Float = {
    h
  }

  def setH(h: Float) {
    this.h = h
  }

  def copy(x: Float, y: Float): AABB = {
    new AABB(x, y, w, h)
  }

  def copy: AABB = {
    new AABB(x, y, w, h)
  }
}