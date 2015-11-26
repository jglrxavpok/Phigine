package org.jglr.phiengine.core

import com.codedisaster.steamworks.SteamAPI
import org.jglr.phiengine.client.input.KeyboardController
import org.jglr.phiengine.client.render.{Shader, Texture}
import org.jglr.phiengine.client.text.{Font, FontRenderer}
import org.jglr.phiengine.client.utils.Timer
import org.jglr.phiengine.core.game.Game
import org.jglr.phiengine.core.io.Assets
import org.jglr.phiengine.core.maths.YepppNativesSetup
import org.jglr.phiengine.core.utils._
import org.jglr.phiengine.network.{NetworkSide, NetworkHandler}
import org.jglr.phiengine.network.channels.PhiChannel
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW._
import org.slf4j.{LoggerFactory, Logger}

abstract class PhigineBase extends IDisposable {

  protected var tickableRegistry: Registry[String, ITickable] = null
  protected var logger: Logger = null
  protected var networkHandler: NetworkHandler = null
  protected var running: Boolean = false

  def init(game: Game, config: PhiConfig) {
    logger = LoggerFactory.getLogger(game.getName)
    logger.info("Loading Phigine "+PhiEngine.getVersion)
    tickableRegistry = new Registry[String, ITickable]
  }

  /**
    * Called when cleaning up the object
    */
  override def dispose(): Unit = {}

  def loop() {
    var delta: Float = 0
    var accumulator: Float = 0f
    val interval: Float = 1f / 60L
    var alpha: Float = 0
    while (running) {
      delta = getDelta
      accumulator += delta
      pollEvents()
      while (accumulator >= interval) {
        update(interval)
        accumulator -= interval
      }
      alpha = accumulator / interval
      render(alpha)
      postLoop()
    }
    dispose()
  }

  def postLoop(): Unit

  def getDelta: Float

  def render(delta: Float): Unit

  def update(delta: Float): Unit

  def pollEvents(): Unit

  def getLogger: Logger = {
    logger
  }

  def getNetworkHandler: NetworkHandler = {
    networkHandler
  }

  def getTickableRegistry: Registry[String, ITickable] = {
    tickableRegistry
  }
}
