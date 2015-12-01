package org.jglr.phiengine.client.render

import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.utils.ITickable
import org.joml.{Matrix4f, Quaternionf, Vector3f}

class Camera(protected val projectionMatrix: Matrix4f) extends ITickable {

  val position: Vector3f = new Vector3f
  val rotation: Quaternionf = new Quaternionf().identity()

  val viewMatrix = new Matrix4f().identity
  private val combined = new Matrix4f().identity
  protected val translationMatrix = new Matrix4f().identity
  protected val rotationMatrix = new Matrix4f().identity

  /**
   * Ticks this object<hr/>
   * For cameras, it means updating the engine's projection matrix
   * @param delta
   * The time in milliseconds between the last two frames
   */
  override def tick(delta: Float): Unit = {
    viewMatrix.identity
    translationMatrix.translation(-position.x, -position.y, -position.z)
    rotationMatrix.rotation(rotation)
    modifyView()
    projectionMatrix.mul(viewMatrix, combined)
    PhiEngine.getInstance.setProjectionMatrix(combined)
  }

  def modifyView(): Unit = {
    viewMatrix.mul(rotationMatrix.mul(translationMatrix))
  }
}
