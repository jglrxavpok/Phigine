package org.jglr.phiengine.core.particle

import org.jglr.phiengine.client.render.{Texture, TextureRegion}
import org.jglr.phiengine.core.level.Level
import org.joml.{Vector4f, Vector3f}

class Particle(val level: Level, val id: String, val position: Vector3f, val velocity: Vector3f, val maxLife: Float, val scale: Float) {

  var currentLife = maxLife
  var gravityEfficiency = 1f
  var angle: Float = 0f
  val color: Vector4f = new Vector4f(1,1,1,1)
  var opacity: Float = 1f

  def update(delta: Float): Unit = {
    val tmpVec = new Vector3f()
    velocity.add(level.getGravity.mul(gravityEfficiency*delta, tmpVec))
    position.add(velocity.mul(delta, tmpVec))
    currentLife -= delta

    // calculate opacity
    opacity = currentLife/maxLife
  }

  def shouldBeKilled: Boolean = {
    currentLife <= 0f
  }

}
