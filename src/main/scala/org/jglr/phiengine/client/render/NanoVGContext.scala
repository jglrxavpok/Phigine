package org.jglr.phiengine.client.render

import org.lwjgl.nanovg.{NVGPaint, NVGColor, NanoVGGL3, NanoVG}
import NanoVG._

/**
  *
  */
class NanoVGContext(val antialiasing: Boolean, val stencilStrokes: Boolean = false, val debugging: Boolean = false) {

  private var flags = 0
  if(antialiasing)
    flags += NanoVGGL3.NVG_ANTIALIAS
  if(stencilStrokes)
    flags += NanoVGGL3.NVG_STENCIL_STROKES
  if(debugging)
    flags += NanoVGGL3.NVG_DEBUG

  val contextID = NanoVGGL3.nvgCreateGL3(flags)
  nvgReset(contextID)
  private val currentColor = NVGColor.create()

  def beginFrame(width: Int, height: Int, ratio: Float = 1f): Unit = {
    nvgBeginFrame(contextID, width, height, ratio)
    nvgGlobalAlpha(contextID, 1f)
  }

  def endFrame(): Unit = {
    nvgEndFrame(contextID)
  }

  def cancelFrame(): Unit = {
    nvgCancelFrame(contextID)
  }

  def rect(x: Float, y: Float, w: Float, h: Float): Unit = {
    nvgRect(contextID, x, y, w, h)
  }

  def roundedRect(x: Float, y: Float, w: Float, h: Float, r: Float): Unit = {
    nvgRoundedRect(contextID, x, y, w, h, r)
  }

  def startPath(): Unit = {
    nvgBeginPath(contextID)
  }

  def endPath(): Unit = {
    nvgClosePath(contextID)
  }

  def setColor(r: Float, g: Float, b: Float, a: Float) = {
    currentColor.r(r)
    currentColor.g(g)
    currentColor.b(b)
    currentColor.a(a)
  }

  def fillColor(r: Float, g: Float, b: Float, a: Float = 1f): Unit = {
    setColor(r, g, b, a)
    nvgFillColor(contextID, currentColor)
  }

  def strokeColor(r: Float, g: Float, b: Float, a: Float = 1f): Unit = {
    setColor(r, g, b, a)
    nvgStrokeColor(contextID, currentColor)
  }

  def fill(): Unit = {
    nvgFill(contextID)
  }

  def stroke(): Unit = {
    nvgStroke(contextID)
  }

  def restore(): Unit = {
    nvgRestore(contextID)
  }

  def save(): Unit = {
    nvgSave(contextID)
  }

  def reset(): Unit = {
    nvgReset(contextID)
  }

  def scissor(x: Int, y: Int, w: Int, h: Int): Unit = {
    nvgScissor(contextID, x, y, w, h)
  }

  def intersectScissor(x: Int, y: Int, w: Int, h: Int): Unit = {
    nvgIntersectScissor(contextID, x, y, w, h)
  }

  def resetScissor(): Unit = {
    nvgResetScissor(contextID)
  }

  def translate(x: Int, y: Int): Unit = {
    nvgTranslate(contextID, x, y)
  }

  def resetTransformation(): Unit = {
    nvgResetTransform(contextID)
  }

  def rotate(angle: Float): Unit = {
    nvgRotate(contextID, angle)
  }

  def circle(cx: Float, cy: Float, r: Float): Unit = {
    nvgCircle(contextID, cx, cy, r)
  }

  def strokeWidth(width: Float): Unit = {
    nvgStrokeWidth(contextID, width)
  }

  def strokePaint(paint: NVGPaint): Unit = {
    nvgStrokePaint(contextID, paint)
  }
}
