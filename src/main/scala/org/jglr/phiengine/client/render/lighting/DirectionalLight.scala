package org.jglr.phiengine.client.render.lighting

import org.jglr.phiengine.client.render.Shader
import org.joml.{Vector3f, Quaternionf, Vector4f}

class DirectionalLight(color: Vector3f) extends Light(color) {
  val rotation: Quaternionf = new Quaternionf().identity()

  override def writeToShader(shader: Shader, prefix: String): Unit = {
    super.writeToShader(shader, s"$prefix.base.")
    shader.setUniform4f(s"$prefix.rotation", rotation)
  }
}
