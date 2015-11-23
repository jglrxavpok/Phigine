package org.jglr.phiengine.client.render.lighting

import org.jglr.phiengine.client.render.Shader
import org.joml.{Vector3f, Vector4f}

abstract class Light(val color: Vector3f) {
  /**
    * Attenuation = a*x^2 + b*x^ + c
    * where (a, b, c) = attenuationCoefficients
    */
  val attenuationCoefficients: Vector3f = new Vector3f(1,1,1)
  var intensity = 1f

  def writeToShader(shader: Shader, prefix: String): Unit = {
    shader.setUniformf(s"$prefix.intensity", intensity)
    shader.setUniform3f(s"$prefix.color", color)

    shader.setUniformf(s"$prefix.attenuation.exponent", attenuationCoefficients.x)
    shader.setUniformf(s"$prefix.attenuation.linear", attenuationCoefficients.y)
    shader.setUniformf(s"$prefix.attenuation.constant", attenuationCoefficients.z)
  }
}
