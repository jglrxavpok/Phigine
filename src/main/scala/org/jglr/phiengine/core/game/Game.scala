package org.jglr.phiengine.core.game

import org.jglr.phiengine.client.input.InputProcessor
import org.jglr.phiengine.client.render.Color
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.maths.Mat4
import org.jglr.phiengine.core.utils.PhiConfig

abstract class Game(val engine: PhiEngine) {
  def pollEvents(): Unit = {}

  def getName: String

  def init(config: PhiConfig)

  def render(delta: Float)

  def update(delta: Float)

  def setProjectionMatrix(m: Mat4) {
    engine.setProjectionMatrix(m)
  }

  def setIcon(icon: FilePointer) {
    engine.setIcon(icon)
  }

  def addInputProcessor(proc: InputProcessor) {
    engine.addInputListener(proc)
  }

  def setBackgroundColor(color: Color) = {
    engine.setBackgroundColor(color)
  }
}