package org.jglr.phiengine.core.utils

import org.jbox2d.common.{Vec2 => BoxVec2}
import org.joml.Vector2f

object Box2DUtils {
  implicit def toBox2D(v2: Vector2f): BoxVec2 = {
    new BoxVec2(v2.x, v2.y)
  }
}
