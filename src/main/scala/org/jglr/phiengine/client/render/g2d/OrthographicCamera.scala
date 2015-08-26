package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.Camera
import org.jglr.phiengine.core.maths.Mat4

class OrthographicCamera(displayWidth: Float, displayHeight: Float) extends Camera(new Mat4().orthographic(0f, displayWidth, displayHeight, 0f, -100, 100f)) {

  var zoom: Float = 1f
  private val scalingMatrix = new Mat4().identity

  override def tick(delta: Float): Unit = {
    scalingMatrix.scale(zoom, zoom, 1f)
    super.tick(delta)
  }

  override def modifyView(): Unit = {
    super.modifyView()
    viewMatrix *= scalingMatrix
  }
}
