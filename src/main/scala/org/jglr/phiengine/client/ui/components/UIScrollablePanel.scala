package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.render.nanovg.NanoCanvas
import org.jglr.phiengine.client.render.{Colors, ScreenLock}
import org.jglr.phiengine.client.render.g2d.{ShapeMode, ShapeBatch, SpriteBatch}
import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.UIComponent
import org.jglr.phiengine.client.ui.layouts.RelativeLayout
import org.jglr.phiengine.core.PhiEngine
import org.joml.Vector3f
import org.lwjgl.opengl.GL11._

class UIScrollablePanel(fontRenderer: FontRenderer, components: UIComponent*) extends UIPanel(fontRenderer) {

  private val shapeBatch = new ShapeBatch(ShapeMode.LINES, maxVertices = 4)
  private val lock = new ScreenLock
  val borderColor = Colors.blue.lighter()
  layout = new RelativeLayout(this)

  for(comp <- components) {
    addChild(comp)
  }

  def drawBorder(delta: Float): Unit = {
    shapeBatch.rectangle(x, y, 0, w, h, borderColor)
  }

  override def render(delta: Float, batch: SpriteBatch, canvas: NanoCanvas): Unit = {
    fontRenderer.batch.flush()
    batch.flush()
    super.render(delta, batch, canvas)
    canvas.flush()
    lock.enableLock(x, y, w, h)
    batch.flush()
    fontRenderer.batch.flush()
    lock.disableLock()

    drawBorder(delta)
  }

}
