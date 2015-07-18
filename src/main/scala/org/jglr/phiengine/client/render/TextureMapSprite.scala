package org.jglr.phiengine.client.render

import java.awt.image._
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.utils.ITickable

class TextureMapSprite extends ITickable {
  var location: FilePointer = null
  var icon: TextureRegion = null
  var rawImage: BufferedImage = null
  var useRawImage: Boolean = false

  def tick(delta: Float) {
  }
}