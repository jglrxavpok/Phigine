import org.jglr.phiengine.core.entity.{UpdateComponent, Component, Entity}
import org.jglr.phiengine.core.maths.Vec3

class PositionComponent(entity: Entity) extends UpdateComponent(entity) {
  val position: Vec3 = new Vec3
  val velocity: Vec3 = new Vec3
  val lastPosition: Vec3 = new Vec3

  override def preUpdate(delta: Float): Unit = {
    lastPosition(position)
  }

  override def update(delta: Float): Unit = {

  }
}
