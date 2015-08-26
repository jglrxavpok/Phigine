package org.jglr.phiengine.core.utils

import org.jglr.phiengine.core.PhiEngine

/**
 * Trait used by classes that can benefit from the auto updating system, automatically registers the instance inside the tickable registry of [[org.jglr.phiengine.core.PhiEngine Phingine]]
 */
trait AutoUpdateable extends ITickable {
  PhiEngine.getInstance.autoUpdate(this)

  def stopUpdating(): Unit = {
    PhiEngine.getInstance.stopAutoUpdate(this)
  }
}
