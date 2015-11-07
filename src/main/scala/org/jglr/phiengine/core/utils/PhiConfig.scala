package org.jglr.phiengine.core.utils

import java.util

/**
 * Helper class used to configurate Phigine at launch
 */
class PhiConfig {
  var autoUpdates: Boolean = false
  var title: String = "PhiEngine game"
  var centered: Boolean = true
  var fullscreen: Boolean = false
  var height: Int = 720
  var width: Int = ((16f / 9f) * height).toInt
  var resizable: Boolean = false
  var decorated: Boolean = true

  /**
   * If set to `true`, textures will be loaded for each instantiation of [[org.jglr.phiengine.client.render.Texture Texture]]<br/>
   * Meanwhile, if set to false, the engine will cache each texture loaded via its [[org.jglr.phiengine.core.io.FilePointer FilePointer]]
   */
  var lazyTextures: Boolean = false

  /**
   * If set to `true`, shaders will be loaded for each instantiation of [[org.jglr.phiengine.client.render.Shader Shader]]<br/>
   * Meanwhile, if set to false, the engine will cache each shader loaded via its [[org.jglr.phiengine.core.io.FilePointer FilePointer]]
   */
  var lazyShaders: Boolean = false

  /**
   * If set to `true`, all textures given inside classpath at location /&lt;game name&gt;/assets.json will be loaded while the engine starts up.
   */
  var loadTexturesAtLaunch: Boolean = false

  /**
   * If set to `true`, all shaders given inside the "preload" part of the [[org.jglr.phiengine.core.io.Assets Asset list]] will be loaded while the engine starts up.
   */
  var loadShadersAtLaunch: Boolean = false

  /**
   * Set to true in order to use the functionalities of Steam
   */
  var usesSteamAPI: Boolean = false

  /**
   * Map containing all the program arguments, filled at start up.<br/>
   * This map is filled and used ''after'' processing of the other variables available in this class.<br/>
   * Engine-specific values can be found [[org.jglr.phiengine.core.EngineStart here]]<br/>
   * '''''Does not necessary contains anything, use `getUserDefined(value, fallback)` if you want a fallback value'''''
   */
  var userDefined: util.HashMap[String, String] = new util.HashMap[String, String]

  def getUserDefined(value: String, fallback: String = "false"): String = {
    userDefined.getOrDefault(value, fallback)
  }

  /**
   * This method is syntax sugar for {{{getUserDefined(key, fallback)}}}
   * @param key
   *            The key to access
   * @param fallback
   *                The value to return in case the `key` does not exist
   * @return
   *         The value attached to the key, or `fallback`
   */
  def apply(key: String, fallback: String = "false"): String = getUserDefined(key, fallback)
}