package org.jglr.phiengine.client.render.g3d

import org.jglr.phiengine.client.render.{Texture, Shader, Mesh}
import org.joml.{Matrix4f, Quaternionf, Vector3f}

class Model(var mesh: Mesh, var texture: Texture) {

  val position: Vector3f = new Vector3f
  val rotation: Quaternionf = new Quaternionf().identity()
  val scale: Vector3f = new Vector3f(1,1,1)
  val modelview: Matrix4f = new Matrix4f().identity()
  val originOffset: Vector3f = new Vector3f()

  def computeModelview(): Matrix4f = {
    modelview.translation(-originOffset.x, -originOffset.y, -originOffset.z).translate(position).rotate(rotation).scale(scale)
  }

  def render(delta: Float, shader: Shader = null): Unit = {
    if(shader != null) {
      shader.bind()
      shader.setUniformMat4("u_modelview", computeModelview())
    }
    mesh.bind()
    texture.bind()
    mesh.render()
  }
}
