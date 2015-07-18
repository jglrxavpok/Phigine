package org.jglr.phiengine.client.render

import java.time.temporal.TemporalAmount
import javax.print.attribute.standard.MediaSize.Other

import org.jglr.phiengine.core.maths.Vec3

object ColorModel extends Enumeration {
  type Type = Value
  val RGBA, ARGB = Value
}

object Colors {
  val gray = new Color(0.5f, 0.5f, 0.5f)
  val darkGray = gray darker()
  val lightGray = gray lighter()

  val black = new Color(0,0,0)
  val white = new Color(1,1,1)
  val red = new Color(1,0,0)
  val green = new Color(0,1,0)
  val blue = new Color(0,0,1)

  val yellow = new Color(1,1,0)
  val cyan = new Color(0,1,1)
  val pink = new Color(1,0,1)

  val niceWhite = create(0xF0, 0xF0, 0xF0)

  def create(r: Int, g: Int, b: Int, a: Int = 255): Color = {
    new Color(r/255f,g/255f,b/255f,a/255f)
  }

  def create(color: Int): Color = {
    val a = color >> 24 & 0xFF
    val r = color >> 16 & 0xFF
    val g = color >> 8 & 0xFF
    val b = color & 0xFF
    create(r, g, b, a)
  }

  implicit def toRGBAFloats(color: Color): (Float, Float, Float, Float) = {
    (color.r, color.g, color.b, color.a)
  }
}

class Color(val r: Float, val g: Float, val b: Float, val a: Float = 1f) {
  def copyWithAlpha(alpha: Float) = {
    new Color(r,g,b,alpha)
  }

  def copy() = {
    new Color(r,g,b,a)
  }

  def lerp(other: Color, alpha: Float): Color = {
    val invAlpha = 1f-alpha
    new Color(r * invAlpha + other.r * alpha, g * invAlpha + other.g * alpha, b * invAlpha + other.b * alpha, a * invAlpha + other.a * alpha)
  }

  def +(other: Color): Color = {
    new Color(r+other.r,g+other.g,b+other.b,a+other.a)
  }


  def -(other: Color): Color = {
    new Color(r-other.r,g-other.g,b-other.b,a-other.a)
  }

  def *(other: Color): Color = {
    new Color(r*other.r,g*other.g,b*other.b,a*other.a)
  }

  def *(ar: Float, ag: Float, ab: Float, aa: Float): Color = {
    new Color(r*ar,g*ag,b*ab,a*aa)
  }

  def *(amount: Float): Color = {
    new Color(r*amount,g*amount,b*amount,a*amount)
  }

  def /(amount: Float) = {
    new Color(r/amount,g/amount,b/amount,a/amount)
  }

  def **(amount: Float): Color = {
    new Color(r*amount,g*amount,b*amount,a)
  }

  def lighter(): Color = {
    this ** 1.5f
  }

  def darker(): Color = {
    this ** 0.25f
  }

  def unary_~(): Color = {
    new Color(1-r, 1-g, 1-b, a)
  }

  def rgb(): Vec3 = {
    new Vec3(r, g, b)
  }

  def toInt(model: ColorModel.Type): Int = {
    val ir: Int = (r * 255f).toInt
    val ig: Int = (g * 255f).toInt
    val ib: Int = (b * 255f).toInt
    val ia: Int = (a * 255f).toInt
    model match {
      case ColorModel.ARGB =>
        (ia << 24) + (ir << 16) + (ig << 8) + ib
      case ColorModel.RGBA =>
        (ir << 24) + (ig << 16) + (ib << 8) + ia
      case _ =>
        0
    }
  }
}
