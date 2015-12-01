package org.jglr.phiengine.core.utils

import javax.vecmath.{Vector3f => vecmVec3f}
import org.joml.{Vector3f => jomlVec3f}

object VecmathUtils {

  implicit def toJOML(value: vecmVec3f): jomlVec3f = {
    new jomlVec3f(value.x, value.y, value.z)
  }

  implicit def toVecmath(value: jomlVec3f): vecmVec3f = {
    new vecmVec3f(value.x, value.y, value.z)
  }

}
