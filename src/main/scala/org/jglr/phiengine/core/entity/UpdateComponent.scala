package org.jglr.phiengine.core.entity

/**
 * A component that is updated when the level is
 * @param entity
 *               The entity on which the component will act
 */
abstract class UpdateComponent(entity: Entity) extends Component(entity) {

  /**
   * Updates the component
   * @param delta
   *              The time, in ms, between the last two frames
   */
  def update(delta: Float): Unit

  /**
   * Called after update calls for all instances of UpdateComponent
   * @param delta
   *              The time, in ms, between the last two frames
   */
  def postUpdate(delta: Float): Unit = {}

  /**
   * Called before update calls for all instances of UpdateComponent
   * @param delta
   *              The time, in ms, between the last two frames
   */
  def preUpdate(delta: Float): Unit = {}
}
