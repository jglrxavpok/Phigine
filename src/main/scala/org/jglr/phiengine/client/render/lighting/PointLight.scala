package org.jglr.phiengine.client.render.lighting

import org.jglr.phiengine.client.render.Shader
import org.joml.{Vector3f, Vector4f}

class PointLight(color: Vector3f) extends Light(color) {
  val pos: Vector3f = new Vector3f()

  override def writeToShader(shader: Shader, prefix: String): Unit = {
    super.writeToShader(shader, s"$prefix.base")
    shader.setUniform3f(s"$prefix.position", pos)
  }
}
