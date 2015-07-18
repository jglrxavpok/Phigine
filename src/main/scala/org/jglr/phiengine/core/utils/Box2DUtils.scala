package org.jglr.phiengine.core.utils

import org.jglr.phiengine.core.maths.Vec2
import org.jbox2d.common.{Vec2 => BoxVec2}

object Box2DUtils {
  implicit def toBox2D(v2: Vec2): BoxVec2 = {
    new BoxVec2(v2.getX, v2.getY)
  }
}
