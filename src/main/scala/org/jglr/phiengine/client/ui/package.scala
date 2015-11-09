package org.jglr.phiengine.client

import org.jglr.phiengine.core.PhiEngine

package object ui {
  val width = PhiEngine.getInstance.getDisplayWidth
  val height = PhiEngine.getInstance.getDisplayHeight
}
