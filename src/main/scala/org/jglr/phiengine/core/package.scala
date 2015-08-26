package org.jglr.phiengine

import org.jglr.phiengine.client.render.{Textures, Shaders}
import org.jglr.phiengine.core.utils.PhiConfig

package object core {

  /**
   * The engine start is an object that handles engine-specific program arguments
   */
  object EngineStart {

    def outputShaderList(value: String): Unit = {
      if(value) {
        Shaders.outputList = value
      }
    }

    def outputTextureList(value: String): Unit = {
      if(value) {
        Textures.outputList = value
      }
    }

    private implicit def toBoolean(string: String): Boolean = {
      if(string == null)
        false
      else if(string.equalsIgnoreCase("true") || string.equalsIgnoreCase("1"))
        true
      else
        false
    }

    def handle(engine: PhiEngine, config: PhiConfig): Unit = {
      engine.autoUpdates = config.autoUpdates
      engine.displayWidth = config.width
      engine.displayHeight = config.height
      Shaders.shouldCache = !config.lazyShaders
      Textures.shouldCache = !config.lazyTextures
      outputShaderList(config("outputShaderList"))
      outputTextureList(config("outputTextureList"))
    }
  }
}
