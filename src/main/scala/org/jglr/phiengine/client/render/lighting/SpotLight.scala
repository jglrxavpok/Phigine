package org.jglr.phiengine.client.render.lighting

import org.jglr.phiengine.client.render.Shader
import org.joml.{Quaternionf, Vector3f, Vector4f}

class SpotLight(color: Vector3f) extends Light(color) {
  val pos: Vector3f = new Vector3f()
  val rotation: Quaternionf = new Quaternionf().identity()
  var halfAngle: Float = (Math.PI/2f).toFloat

  override def writeToShader(shader: Shader, prefix: String): Unit = {
    super.writeToShader(shader, s"$prefix.base")
    shader.setUniform4f(s"$prefix.rotation", rotation)
    shader.setUniform3f(s"$prefix.position", pos)
    shader.setUniformf(s"$prefix.halfAngle", halfAngle)
  }
}
