package org.jglr.phiengine.client.ui

import java.util.{List, ArrayList}

import org.jglr.phiengine.client.input.InputProcessor
import org.jglr.phiengine.client.render.nanovg.NanoCanvas
import org.jglr.phiengine.client.render.{TextureRegion, TextureMapSprite, TextureMap}
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.{FontRenderer, Font}
import org.jglr.phiengine.client.ui.UIComponent
import org.jglr.phiengine.client.ui.components.{ButtonTextures, UIButton}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.utils.AutoUpdateable
import org.lwjgl.nanovg.NanoVG

import scala.reflect.ClassTag

object UITextures extends TextureMap(new FilePointer("assets/textures/ui/")) {
  PhiEngine.getInstance.displayLoadingStep("Loading UI textures")
  PhiEngine.getInstance.displayLoadingStep("  Loading buttons textures")
  ButtonTextures.genTextures()
  compile
  writeDebugTexture()
}

class UI(var fontRenderer: FontRenderer = null) extends UIComponent(fontRenderer) with AutoUpdateable {
  shouldAutoUpdate = false
  val engine = PhiEngine.getInstance
  var newMenu: UIComponent = null
  var currentMenu: UIComponent = null

  if(fontRenderer == null) {
    fontRenderer = new FontRenderer(FontRenderer.ASCII, Font.get("Arial", 28, antialias = false))
  }

  val canvas = new NanoCanvas(antialiasing = true)
  val batch = new SpriteBatch(500)
  batch.setTexture(UITextures)

  w = engine.getDisplayWidth
  h = engine.getDisplayHeight

  engine.addInputListener(this)

  override def render(delta: Float, batch: SpriteBatch = batch, canvas: NanoCanvas = canvas): Unit = {
    var wasDrawing = true

    canvas.resetState()
    canvas.resetScissor()
    canvas.resetTransformation()
    canvas.startDrawing(w.toInt, h.toInt)
    fontRenderer.batch.begin()
    if(!batch.isDrawing) {
      wasDrawing = false
    }
    batch.setTexture(UITextures)
    super.render(delta, batch, canvas)
    canvas.stopDrawing()
    if(!wasDrawing) {
      batch.end()
    }

    if(fontRenderer.batch.isDrawing) {
      fontRenderer.batch.end()
    }
  }

  /**
   * Ticks this object
   * @param delta
   * The time in milliseconds between the last two frames
   */
  override def tick(delta: Float): Unit = {
    update(delta)
  }

  override def update(delta: Float): Unit = {
    if(newMenu != currentMenu) {
      if(currentMenu != null)
        removeChild(currentMenu)
      if(newMenu != null) {
        addChild(newMenu)
      }
      currentMenu = newMenu
    }
    super.update(delta)
  }

  def openMenu(menu: UIComponent): Unit = {
    newMenu = menu
  }

  def quickMenu(clazz: Class[_<:UIComponent]): Unit = {
    val instance = clazz.getConstructor(classOf[FontRenderer]).newInstance(fontRenderer)
    openMenu(instance)
  }
}
