package org.jglr.phiengine.core.level

import java.util.{ArrayList, List}

import org.jglr.phiengine.client.render.g2d.G2DComponent
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.entity.{UpdateComponent, Entity, Component}
import org.jglr.phiengine.core.utils.{AutoUpdateable, ITickable}
import org.jglr.phiengine.core.utils.JavaConversions._

abstract class Level extends AutoUpdateable {

  def createEntity(): Entity = {
    val ent = new Entity(this)
    onEntityCreation(ent)
    ent
  }

  def onEntityCreation(entity: Entity): Unit

  def tick(delta: Float): Unit = {
    update(delta)
  }

  val entities = new ArrayList[Entity]
  val spawnQueue = new ArrayList[Entity]
  val despawnQueue = new ArrayList[Entity]
  var updating = false

  def despawnEntity(entity: Entity, ignoreUpdating: Boolean = false): Unit = {
    if(updating && !ignoreUpdating) {
      despawnQueue.add(entity)
    } else {
      entities.remove(entity)
      onEntityDespawn(entity)
    }
  }

  def spawnEntity(entity: Entity, ignoreUpdating: Boolean = false): Unit = {
    if(updating && !ignoreUpdating) {
      spawnQueue.add(entity)
    } else {
      entities.add(entity)
      onEntitySpawn(entity)
    }
  }

  def components[A<:Component](comp: Class[A]): List[A] = {
    val result = new ArrayList[A]
    entities.forEach((e: Entity) => {
      result.addAll(e.getComponents(comp))
    })
    result
  }

  def update(delta: Float): Unit = {
    updating = true
    despawnQueue.forEach((e: Entity) => {
      despawnEntity(e, true)
    })
    spawnQueue.forEach((e: Entity) => {
      spawnEntity(e, true)
    })
    despawnQueue.clear()
    spawnQueue.clear()
    updateLevel(delta)
    entities.forEach((e: Entity) => {
      val list = e.getComponents(classOf[UpdateComponent])
      list.forEach((comp: UpdateComponent) => comp.preUpdate(delta))
      list.forEach((comp: UpdateComponent) => comp.update(delta))
      list.forEach((comp: UpdateComponent) => comp.postUpdate(delta))
    })
    updating = false
  }

  def onEntityDespawn(entity: Entity): Unit

  def onEntitySpawn(entity: Entity): Unit

  def updateLevel(delta: Float)
}