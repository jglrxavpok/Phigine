package org.jglr.phiengine.core.particle

import java.util

import org.jglr.phiengine.core.utils.ITickable
import scala.collection.JavaConversions._

class ParticleSystem(val maxParticleCount: Int = 1000) extends ITickable {

  private val particleList = new util.LinkedList[Particle]()
  private val removalList = new util.LinkedList[Particle]()

  /**
    * Ticks this object
    * @param delta
   * The time in milliseconds between the last two frames
    */
  override def tick(delta: Float): Unit = {
    for(p <- particleList) {
      p.update(delta)
      if(p.shouldBeKilled) {
        removalList.add(p)
      }
    }
    particleList.removeAll(removalList)
    removalList.clear()
  }

  def addParticle(particle: Particle): Boolean = {
    if(particleCount >= maxParticleCount) {
      particleList.removeFirst()
    }
    particleList.add(particle)
  }

  def removeParticle(particle: Particle): Boolean = {
    particleList.remove(particle)
  }

  def removeAll(): Unit = {
    particleList.clear()
  }

  def particleCount: Int = {
    particleList.size
  }

  def getParticles: util.List[Particle] = {
    particleList
  }

}
