package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.Camera
import org.joml.Matrix4f

class OrthographicCamera(displayWidth: Float, displayHeight: Float) extends Camera(new Matrix4f().setOrtho2D(0f, displayWidth, displayHeight, 0f)) {

  var zoom: Float = 1f
  private val scalingMatrix = new Matrix4f().identity()

  override def tick(delta: Float): Unit = {
    scalingMatrix.scaling(zoom, zoom, 1f)
    super.tick(delta)
  }

  override def modifyView(): Unit = {
    super.modifyView()
    viewMatrix.mul(scalingMatrix)
  }
}
