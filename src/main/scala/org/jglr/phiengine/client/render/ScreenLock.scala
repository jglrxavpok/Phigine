package org.jglr.phiengine.client.render

import org.jglr.phiengine.client.render.g2d.{SpriteBatch, Sprite}
import org.jglr.phiengine.client.render.g3d.Model
import org.jglr.phiengine.client.ui
import org.joml.Vector4f
import org.lwjgl.opengl.GL11._

class ScreenLock {

  private val batch = new SpriteBatch(1)
  private val model = new Sprite(new Texture("virtual:null"))
  model.setWidth(1f)
  model.setHeight(1f)
  val id = (Math.random()*0xFE).toInt +1 // ranges from 1 to 255 (included)

  private def setRegion(x: Float, y: Float, w: Float, h: Float): Unit = {
    // draw the mask
    glStencilMask(0xFF)
    glStencilFunc(GL_ALWAYS, id, 0xFF)
    glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)

    // render a quad representing the region
    model.setPosition(x, y)
    model.setWidth(w)
    model.setHeight(h)
    batch.begin()
    model.draw(batch)
    batch.end()
  }

  def enableLock(x: Float, y: Float, w: Float, h: Float): Unit = {
    glColorMask(false, false, false, false)
    glDepthMask(false)
    glEnable(GL_STENCIL_TEST)
    glStencilMask(0xFF)
    glClearStencil(0)
    glClear(GL_STENCIL_BUFFER_BIT)

    setRegion(x, y, w, h)
    glDepthMask(true)
    glColorMask(true, true, true, true)
    glStencilMask(0x00)
    glStencilFunc(GL_EQUAL, id, 0xFF)
  }

  def disableLock(): Unit = {
    glDisable(GL_STENCIL_TEST)
  }
}
