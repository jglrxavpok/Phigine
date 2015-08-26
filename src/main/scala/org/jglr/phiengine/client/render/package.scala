package org.jglr.phiengine.client

import org.jglr.phiengine.core.PhiEngine

package object render {
  def checkGLError(message: String): Unit = {
    PhiEngine.getInstance.checkGLError(message)
  }
}
