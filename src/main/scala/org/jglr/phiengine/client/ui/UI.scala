package org.jglr.phiengine.client.ui

import java.util.{List, ArrayList}

import org.jglr.phiengine.client.input.InputProcessor
import org.jglr.phiengine.client.render.{TextureRegion, TextureMapSprite, TextureMap, Texture}
import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.{FontRenderer, Font}
import org.jglr.phiengine.client.ui.components.{ButtonTextures, UIButton}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.FilePointer

object UITextures extends TextureMap(new FilePointer("assets/textures/ui/")) {
  ButtonTextures.genTextures()
  compile
  writeDebugTexture()
}

class UI(var fontRenderer: FontRenderer = null) extends UIComponent(fontRenderer) {

  val engine = PhiEngine.getInstance()

  if(fontRenderer == null) {
    fontRenderer = new FontRenderer(FontRenderer.ASCII, Font.get("Arial", 28, false))
  }

  val batch = new SpriteBatch()
  batch.setTexture(UITextures)

  w = engine.getDisplayWidth()
  h = engine.getDisplayWidth()

  engine.addInputListener(this)

  override def render(delta: Float, batch: SpriteBatch = batch): Unit = {
    super.render(delta, batch)
  }
}
