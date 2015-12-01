package org.jglr.phiengine.client.render.particle

import java.util
import java.util.{Comparator, Collections}

import org.jglr.phiengine.client.render.{Camera, TemplateMeshes, Shader}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.maths.MathHelper
import org.jglr.phiengine.core.particle.{ParticleSystem, Particle}
import org.jglr.phiengine.core.utils.IDisposable
import org.joml.{Vector4f, Vector3f, Quaternionf, Matrix4f}
import org.lwjgl.opengl.GL11._
import scala.collection.JavaConversions._

class ParticleRenderer extends IDisposable {

  val mesh = TemplateMeshes.buildPlane(0,1,1)
  val shader = new Shader("assets/shaders/passes/particles.glsl")
  private val particleComparator = new ParticleComparator

  /**
    * Called when cleaning up the object
    */
  override def dispose(): Unit = {
    mesh.dispose()
    shader.dispose()
  }

  def render(particleSystem: ParticleSystem, camera: Camera, delta: Float): Unit = {
    render(particleSystem.getParticles, camera, delta)
  }

  def createModelview(p: Particle, camera: Camera): Matrix4f = {
    val modelview = new Matrix4f().translation(p.position)

    // transpose rotation part of viewMatrix into modelview matrix in order to have the particle quads always facing the camera
    // thanks ThinMatrix!
    modelview.m00 = camera.viewMatrix.m00
    modelview.m10 = camera.viewMatrix.m01
    modelview.m20 = camera.viewMatrix.m02
    modelview.m01 = camera.viewMatrix.m10
    modelview.m11 = camera.viewMatrix.m11
    modelview.m21 = camera.viewMatrix.m12
    modelview.m02 = camera.viewMatrix.m20
    modelview.m12 = camera.viewMatrix.m21
    modelview.m22 = camera.viewMatrix.m22

    modelview.rotate(p.angle, 0, 0, 1)
    modelview.rotate((Math.PI/2f).toFloat, 0, 1, 0)
    modelview.scale(p.scale)

    modelview
  }

  def sortParticles(particles: util.List[Particle], camera: Camera) = {
    particleComparator.cameraPos.set(camera.position)
    Collections.sort(particles, particleComparator)
  }

  def render(particles: util.List[Particle], camera: Camera, delta: Float): Unit = {
    shader.bind()
    mesh.bind()
    ParticleTextureMap.bindMap()
    glDepthMask(false)
    sortParticles(particles, camera)
    for(p <- particles) {
      val modelview = createModelview(p, camera)
      shader.setUniformMat4("u_modelview", modelview)
      shader.setUniform4f("u_particleColor", p.color)
      shader.setUniformf("u_particleOpacity", p.opacity)

      val region = ParticleTextureMap.getRegion(p.id)
      shader.setUniform4f("u_particleRegion", new Vector4f(region.getMinU, region.getMinV, region.getMaxU, region.getMaxV))
      mesh.render()
    }
    glDepthMask(true)
    mesh.unbind()
    shader.unbind()
  }

  private class ParticleComparator extends Comparator[Particle] {

    val cameraPos: Vector3f = new Vector3f

    override def compare(a: Particle, b: Particle): Int = {
      val camDistA = a.position.sub(cameraPos, new Vector3f())
      val camDistB = b.position.sub(cameraPos, new Vector3f())
      -camDistA.length().compareTo(camDistB.length())
    }
  }

}
