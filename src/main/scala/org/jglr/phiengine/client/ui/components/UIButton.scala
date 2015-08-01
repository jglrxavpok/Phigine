package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.render.g2d.SpriteBatch
import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.{ComponentState, ComponentTextures, UIComponent}
import org.jglr.phiengine.client.ui.layouts.RelativeLayout

object ButtonTextures extends ComponentTextures("button_") {

  def genTextures(): Unit = {
    // register idle textures
    register("tleft", ComponentState.IDLE)
    register("bleft", ComponentState.IDLE)
    register("tright", ComponentState.IDLE)
    register("bright", ComponentState.IDLE)
    register("north", ComponentState.IDLE)
    register("east", ComponentState.IDLE)
    register("west", ComponentState.IDLE)
    register("south", ComponentState.IDLE)
    register("center", ComponentState.IDLE)

    // register focused textures
    register("tleft", ComponentState.FOCUSED, "_focused")
    register("bleft", ComponentState.FOCUSED, "_focused")
    register("tright", ComponentState.FOCUSED, "_focused")
    register("bright", ComponentState.FOCUSED, "_focused")
    register("north", ComponentState.FOCUSED, "_focused")
    register("east", ComponentState.FOCUSED, "_focused")
    register("west", ComponentState.FOCUSED, "_focused")
    register("south", ComponentState.FOCUSED, "_focused")
    register("center", ComponentState.FOCUSED, "_focused")

    // register hovered textures
    register("tleft", ComponentState.HOVERED, "_hovered")
    register("bleft", ComponentState.HOVERED, "_hovered")
    register("tright", ComponentState.HOVERED, "_hovered")
    register("bright", ComponentState.HOVERED, "_hovered")
    register("north", ComponentState.HOVERED, "_hovered")
    register("east", ComponentState.HOVERED, "_hovered")
    register("west", ComponentState.HOVERED, "_hovered")
    register("south", ComponentState.HOVERED, "_hovered")
    register("center", ComponentState.HOVERED, "_hovered")
  }

}

class UIButton(fontRenderer: FontRenderer, text: String = null) extends UIComponent(fontRenderer) {

  layout = new RelativeLayout(this)

  if(text != null) {
    addChild(new UILabel(fontRenderer, text))
    pack()
  }

  override def renderSelf(delta: Float, batch: SpriteBatch): Unit = {
    val sw = 8f
    val sh = 8f
    batch.begin()
    val state = ComponentState.FOCUSED // TODO: Changed based on real state

    var i1: Float = sw
    var j1: Float = sh
    while(j1 < h-sh) {
      while(i1 < w-sw) {
        batch.draw(ButtonTextures.get("center", state), x+i1, y+j1, z, sw, sh)
        i1 += sw
      }
      i1 = sw
      j1 += sh
    }

    var i: Float = x+sw
    while(i <= x+w-sw) {
      batch.draw(ButtonTextures.get("south", state), i, y, z, sw, sh)
      batch.draw(ButtonTextures.get("north", state), i, y+h-sh, z, sw, sh)
      i+=sw
    }

    var j: Float = y+sh
    while(j <= y+h-sh) {
      batch.draw(ButtonTextures.get("west", state), x, j, z, sw, sh)
      batch.draw(ButtonTextures.get("east", state), x+w-sw, j, z, sw, sh)
      j+=sh
    }

    batch.draw(ButtonTextures.get("bleft", state), x, y, z, sw, sh)
    batch.draw(ButtonTextures.get("bright", state), x+w-sw, y, z, sw, sh)
    batch.draw(ButtonTextures.get("tleft", state), x, y+h-sh, z, sw, sh)
    batch.draw(ButtonTextures.get("tright", state), x+w-sw, y+h-sh, z, sw, sh)
    batch.end()
  }

  override def onMoved(): Unit = layout.recalculatePositions()
}
