package org.jglr.phiengine.client.render.lighting

import org.jglr.phiengine.core.entity.Entity
import org.jglr.phiengine.core.level.Level

class LightEntity(level: Level, light: Light) extends Entity(level) {
  addComponent(new LightComponent(this, light))
}
