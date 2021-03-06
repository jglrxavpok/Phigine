package org.jglr.phiengine.core.level

import java.util
import java.util.{UUID, ArrayList, List}

import org.jglr.phiengine.core.entity._
import org.jglr.phiengine.core.particle.{Particle, ParticleSystem}
import org.jglr.phiengine.core.utils.AutoUpdateable
import org.jglr.phiengine.core.utils.JavaConversions._
import org.joml.Vector3f

import scala.collection.JavaConversions._

/**
 * A level is a collection of [[org.jglr.phiengine.core.entity.Entity entities]] that interact with each other.
 * It allows addition and removal of entities.
 * If you want to render the level, use [[org.jglr.phiengine.client.render.LevelRenderer LevelRenderer]]
 */
abstract class Level extends AutoUpdateable {

  /**
    * Creates an entity in this level.
    * '''''The created entity is NOT spawned in this level, it must be made manually'''''
    * @param prototype
    *                  The prototype to create the entity from, might be skipped
    * @return
    *         The newly created entity
    */
  def createEntity(prototype: EntityPrototype = EmptyPrototype): Entity = {
    val ent = new Entity(this)
    onEntityCreation(ent)
    prototype(ent)
    ent
  }

  /**
    * Called on entity created, useful for adding level-specific components
    * @param entity
    *               The entity being created
    */
  def onEntityCreation(entity: Entity): Unit

  /**
    * Ticks the level
    * @param delta
    *              The delta time between the last two frames
    */
  def tick(delta: Float): Unit = {
    update(delta)
  }

  /**
    * Collection of all particle system inside this level
    */
  val particleSystems = new util.ArrayList[ParticleSystem]

  /**
    * The main particle system used by this level. Used by the `addParticle` and `removeParticle` methods
    */
  val mainParticleSystem = new ParticleSystem

  /**
    * Map containing the ID of each entity in this level.<br/>
    * Calling <pre>entitiesID.get(ent)</pre> will yield the same result as <pre>ent.id</pre>
    */
  val entitiesID = new util.HashMap[Entity, UUID]()

  /**
    * Collection containing all entities inside this level
    */
  val entities = new util.ArrayList[Entity]

  /**
    * Collection of entities waiting for the end of update to be spawned in
    */
  val spawnQueue = new util.ArrayList[Entity]
  /**
    * Collection of entities waiting for the end of update to be despawned (or removed) from this level
    */
  val despawnQueue = new util.ArrayList[Entity]
  /**
    * Is the level currently in the middle of an update?
    */
  var updating = false

  addParticleSystem(mainParticleSystem)

  /**
    * Despawns an entity from this level
    * @param entity
    *               The entity to despawn
    * @param ignoreUpdating
    *                       Should we ignore the fact that we are updating? Users are advised to let it to default
    */
  def despawnEntity(entity: Entity, ignoreUpdating: Boolean = false): Unit = {
    if(updating && !ignoreUpdating) {
      despawnQueue.add(entity)
    } else {
      entities.remove(entity)
      onEntityDespawn(entity)
    }
  }

  /**
    * Spawns the entity inside the level
    * @param entity
    *               The entity to spawn in
    * @param ignoreUpdating
    *                       Should we ignore the fact that we are updating? Users are advised to let it to default
    */
  def spawnEntity(entity: Entity, ignoreUpdating: Boolean = false): Unit = {
    if(updating && !ignoreUpdating) {
      spawnQueue.add(entity)
    } else {
      entities.add(entity)
      onEntitySpawn(entity)
    }
  }

  /**
    * Returns a list of all the components of all the entities inside the level
    * @param comp
    *             The component class
    * @tparam A
    *           The component type
    * @return
    *         A list containing all the components of all entities
    */
  def components[A<:Component](comp: Class[A]): util.List[A] = {
    val result = new util.ArrayList[A]
    entities.forEach((e: Entity) => {
      result.addAll(e.getComponents(comp))
    })
    result
  }

  /**
    * Updates the level and its entities
    * @param delta
    *              The delta time, in ms, between the last two frames
    */
  def update(delta: Float): Unit = {
    updating = true
    despawnQueue.forEach((e: Entity) => {
      despawnEntity(e, ignoreUpdating = true)
    })
    spawnQueue.forEach((e: Entity) => {
      spawnEntity(e, ignoreUpdating = true)
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
    updateParticles(delta)
    updating = false
  }

  def updateParticles(delta: Float): Unit = {
    for(sys <- particleSystems) {
      sys.tick(delta)
    }
  }

  /**
    * Called when an entity despawns
    * @param entity
    *               The entity to despawn
    */
  def onEntityDespawn(entity: Entity): Unit

  /**
    * Called when an entity spawns
    * @param entity
    *               The entity to spawn
    */
  def onEntitySpawn(entity: Entity): Unit

  /**
    * Update the level itself, should not be used to update entities
    * @param delta
    *              The time between the last two frames
    */
  def updateLevel(delta: Float)

  /**
    * Checks if a given entity is inside this level
    * @param entity
    *               The entity to check
    * @return
    *         true if `entity` is inside the level
    */
  def hasEntity(entity: Entity): Boolean = entities.contains(entity)

  /**
    * Gets the ID of each entity in this level.<br/>
    * Calling <pre>getEntityID(ent)</pre> will yield the same result as <pre>ent.id</pre>
    */
  def getEntityID(entity: Entity): UUID = {
    if(!entitiesID.containsKey(entity)) {
      entitiesID.put(entity, UUID.randomUUID())
    }
    entitiesID.get(entity)
  }

  def addParticleSystem(p: ParticleSystem): Boolean = {
    particleSystems.add(p)
  }

  def removeParticleSystem(p: ParticleSystem): Boolean = {
    particleSystems.remove(p)
  }

  def addParticle(p: Particle): Boolean = {
    mainParticleSystem.addParticle(p)
  }

  def removeParticle(p: Particle): Boolean = {
    mainParticleSystem.removeParticle(p)
  }

  def getGravity: Vector3f = new Vector3f(0,0,0)
}
