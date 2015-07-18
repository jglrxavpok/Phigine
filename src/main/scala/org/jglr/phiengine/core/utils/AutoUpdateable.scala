package org.jglr.phiengine.core.utils

import org.jglr.phiengine.core.PhiEngine

abstract class AutoUpdateable extends ITickable {
  PhiEngine.getInstance().autoUpdate(this)
}
