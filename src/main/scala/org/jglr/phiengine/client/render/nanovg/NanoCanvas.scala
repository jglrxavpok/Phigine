package org.jglr.phiengine.client.render.nanovg

import java.nio.FloatBuffer
import java.util

import org.jglr.phiengine.client.render.Color
import org.jglr.phiengine.client.render.nanovg.NVGFont
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.utils.Buffers
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NanoVG._
import org.lwjgl.nanovg._
import org.lwjgl.system.MemoryUtil

/**
  *
  */
class NanoCanvas(val antialiasing: Boolean, val stencilStrokes: Boolean = false, val debugging: Boolean = false) {

  private var flags = 0
  if(antialiasing)
    flags += NanoVGGL3.NVG_ANTIALIAS
  if(stencilStrokes)
    flags += NanoVGGL3.NVG_STENCIL_STROKES
  if(debugging)
    flags += NanoVGGL3.NVG_DEBUG

  val contextID = NanoVGGL3.nvgCreateGL3(flags)
  nvgReset(contextID)
  private val id2font = new util.HashMap[Int, NVGFont]
  private val name2font = new util.HashMap[String, NVGFont]
  private val currentColor = NVGColor.create()
  private var width = 0
  private var height = 0
  private var ratio = 0f

  def startDrawing(width: Int, height: Int, ratio: Float = 1f): Unit = {
    this.width = width
    this.height = height
    this.ratio = ratio
    nvgBeginFrame(contextID, width, height, ratio)
    nvgGlobalAlpha(contextID, 1f)
  }

  def stopDrawing(): Unit = {
    nvgEndFrame(contextID)
  }

  def cancelDrawing(): Unit = {
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

  def strokeColor(color: Color): Unit = {
    strokeColor(color.r, color.g, color.b, color.a)
  }

  def fillColor(color: Color): Unit = {
    fillColor(color.r, color.g, color.b, color.a)
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

  def restoreState(): Unit = {
    nvgRestore(contextID)
  }

  def saveState(): Unit = {
    nvgSave(contextID)
  }

  def resetState(): Unit = {
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

  def scale(factor: Float): Unit = {
    scale(factor, factor)
  }

  def scale(x: Float, y: Float): Unit = {
    nvgScale(contextID, x, y)
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

  def fontSize(size: Float): Unit = {
    nvgFontSize(contextID, size)
  }

  def drawString(text: String, x: Float, y: Float, end: Long = MemoryUtil.NULL): Unit = {
    nvgText(contextID, x, y, text, end)
  }

  private def registerFont(font: NVGFont) = {
    id2font.put(font.id, font)
    name2font.put(font.name, font)
  }

  def createFontFromMemory(name: String, path: FilePointer, freeData: Boolean = true): NVGFont = {
    val buffer = Buffers.wrapByte(path.readAll)
    val free = if(freeData) 1 else 0
    val id = nvgCreateFontMem(contextID, name, buffer, free)
    val font = new NVGFont(name, id)
    registerFont(font)
    font
  }

  def createFont(name: String, filename: String): NVGFont = {
    val id = nvgCreateFont(contextID, name, filename)
    val font = new NVGFont(name, id)
    registerFont(font)
    font
  }

  def findFont(fontName: String): NVGFont = {
    val id = findFontID(fontName)
    if(!id2font.containsKey(id)) {
      registerFont(new NVGFont(fontName, id))
    }
    id2font.get(id)
  }

  def findFontID(fontName: String): Int = {
    nvgFindFont(contextID, fontName)
  }

  def font(fontObject: NVGFont): Unit = {
    font(fontObject.id)
  }

  def font(fontName: String): Unit = {
    nvgFontFace(contextID, fontName)
  }

  def font(fontID: Int): Unit = {
    nvgFontFaceId(contextID, fontID)
  }

  def fontMetrics(text: String, ascender: FloatBuffer, descender: FloatBuffer, height: FloatBuffer): Unit = {
    nvgTextMetrics(contextID, ascender, descender, height)
  }

  def fontGlyphPositions(text: String): NVGGlyphPosition.Buffer = {
    val positions = new NVGGlyphPosition.Buffer(BufferUtils.createByteBuffer(NVGGlyphPosition.SIZEOF*text.length))
    nvgTextGlyphPositions(contextID, 0, 0, text, MemoryUtil.NULL, positions)
    positions
  }

  def fontWidth(text: String): Float = {
    fontGlyphPositions(text).maxx()
  }

  def fontHeight(text: String): Float = {
    val height = BufferUtils.createFloatBuffer(1)
    fontMetrics(text, null, null, height)
    height.get()
  }

  def flush(): Unit = {
    stopDrawing()
    startDrawing(width, height, ratio)
  }
}
