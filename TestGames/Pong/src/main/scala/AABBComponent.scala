import org.jglr.phiengine.core.entity.{UpdateComponent, Component, Entity}
import org.jglr.phiengine.core.maths.AABB

class AABBComponent(entity: Entity) extends UpdateComponent(entity) {

  val box: AABB = new AABB(0,0,1,1)

  def setSize(width: Float, height: Float): AABBComponent = {
    box.setW(width)
    box.setH(height)
    this
  }

  def setPos(x: Float, y: Float): AABBComponent = {
    box.setX(x)
    box.setY(y)
    this
  }

  override def update(delta: Float): Unit = {
    if(entity.has(classOf[PositionComponent])) {
      val pos = entity.getComponent(classOf[PositionComponent]).position
      box.setX(pos.x)
      box.setY(pos.y)
    }
  }
}
