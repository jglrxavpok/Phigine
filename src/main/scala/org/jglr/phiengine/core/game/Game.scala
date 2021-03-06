package org.jglr.phiengine.core.game

import org.jglr.phiengine.client.input.{InputListener, InputProcessor}
import org.jglr.phiengine.client.render.{Texture, Color}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.level.Level
import org.jglr.phiengine.core.utils.PhiConfig
import org.joml.Matrix4f

/**
 * Base class for Phingine games, Game provides convenience methods to change the engine's state, and update and render itself
 * @param engine
 *               The engine with which the game was created
 */
abstract class Game(val engine: PhiEngine) {

  /**
   * Called when polling events (window, keyboard, mouse, gamepads, etc.)
   */
  def pollEvents(): Unit = {}

  /**
   * Returns the name of the game
   * @return
   *         The game name
   */
  def getName: String

  /**
   * Inits the game
   * @param config
   *               The configuration used to launch the game
   */
  def init(config: PhiConfig)

  /**
   * Renders the game
   * @param delta
   *              Time in milliseconds between two frames
   */
  def render(delta: Float)

  /**
   * Updates the game
   * @param delta
   *              Time in milliseconds between two frames
   */
  def update(delta: Float)

  /**
   * Sets the engine's projection matrix
   * @param m
   *          The projection matrix
   */
  def setProjectionMatrix(m: Matrix4f) {
    engine.setProjectionMatrix(m)
  }

  /**
   * Sets the window icon
   * @param icon
   *             The path of the icon
   */
  def setIcon(icon: FilePointer) {
    engine.setIcon(icon)
  }

  /**
   * Adds an input listener to the engine
   * @param listener
   *             The listener
   */
  def addInputListener(listener: InputListener) {
    engine.addInputListener(listener)
  }

  /**
   * Sets the background color of the game
   * @param color
   *              The background color
   */
  def setBackgroundColor(color: Color) = {
    engine.setBackgroundColor(color)
  }

  /**
   * Returns the logo of the game, if any
   * @return
   *         The game logo
   */
  def getLogo: Texture = null

  /**
   * Returns the id of the game used to locate the [[org.jglr.phiengine.core.io.Assets Assets file]], for instance.<br/>
   * Defaults to the game name
   * @return
   *         The game ID
   */
  def id: String = getName

  def grabMouse(): Unit = engine.grabMouse()

  def ungrabMouse(): Unit = engine.ungrabMouse()

  /**
    * Used by the engine in order
    * @return
    */
  def getLevel: Level = null

}