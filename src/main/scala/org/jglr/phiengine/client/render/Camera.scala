package org.jglr.phiengine.client.render

import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.maths.{Vec3, Mat4}
import org.jglr.phiengine.core.utils.ITickable

abstract class Camera(protected val projectionMatrix: Mat4) extends ITickable {

  val position: Vec3 = new Vec3

  protected val viewMatrix = new Mat4().identity
  private val combined = new Mat4().identity
  protected val translationMatrix = new Mat4().identity

  /**
   * Ticks this object<hr/>
   * For cameras, it means updating the engine's projection matrix
   * @param delta
   * The time in milliseconds between the last two frames
   */
  override def tick(delta: Float): Unit = {
    viewMatrix.identity
    translationMatrix.translation(position.x, position.y, position.z)
    modifyView()
    combined.set(projectionMatrix) *= viewMatrix
    PhiEngine.getInstance.setProjectionMatrix(combined)
  }

  def modifyView(): Unit = {
    viewMatrix *= translationMatrix
  }
}
