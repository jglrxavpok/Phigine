package org.jglr.phiengine.client.input

import com.google.common.collect.Lists
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.List
import org.jglr.phiengine.core.utils.JavaConversions._

object InputProcessorQueue {
  private val KEY_PRESS: Int = 0xA
  private val KEY_RELEASE: Int = 0xB
  private val KEY_TYPED: Int = 0xC
  private val MOUSE_MOVED: Int = 0xD
  private val MOUSE_BUTTON_PRESSED: Int = 0xE
  private val MOUSE_BUTTON_RELEASED: Int = 0xF
  private val SCROLL: Int = 0x9
}

class InputProcessorQueue(inputHandler: InputHandler) extends InputProcessor {
  private val receivers: List[InputListener] = Lists.newArrayList(inputHandler)
  private val queue: List[Integer] = new ArrayList[Integer]

  override def onKeyPressed(keycode: Int): Boolean = {
    queue.add(InputProcessorQueue.KEY_PRESS)
    queue.add(keycode)
    true
  }

  override def onKeyReleased(keycode: Int): Boolean = {
    queue.add(InputProcessorQueue.KEY_RELEASE)
    queue.add(keycode)
    false
  }

  override def onKeyTyped(character: Char): Boolean = {
    queue.add(InputProcessorQueue.KEY_TYPED)
    queue.add(character.toInt)
    false
  }

  override def onMousePressed(screenX: Int, screenY: Int, button: Int): Boolean = {
    queue.add(InputProcessorQueue.MOUSE_BUTTON_PRESSED)
    queue.add(screenX)
    queue.add(screenY)
    queue.add(button)
    false
  }

  override def onMouseReleased(screenX: Int, screenY: Int, button: Int): Boolean = {
    queue.add(InputProcessorQueue.MOUSE_BUTTON_RELEASED)
    queue.add(screenX)
    queue.add(screenY)
    queue.add(button)
    false
  }

  override def onMouseMoved(screenX: Int, screenY: Int): Boolean = {
    queue.add(InputProcessorQueue.MOUSE_MOVED)
    queue.add(screenX)
    queue.add(screenY)
    false
  }

  override def onScroll(dir: Int): Boolean = {
    queue.add(InputProcessorQueue.SCROLL)
    queue.add(dir)
    false
  }

  def addReceiver(receiver: InputListener) {
    receivers.add(receiver)
  }

  def drain() {
    while (!queue.isEmpty) {
      val `type`: Int = queue.remove(0)
      `type` match {
        case InputProcessorQueue.KEY_PRESS =>
          val keyPressed: Int = queue.remove(0)
          receivers.forEach((r: InputListener) => {
            r.onKeyPressed(keyPressed)
          })
        case InputProcessorQueue.KEY_RELEASE =>
          val keyReleased: Int = queue.remove(0)
          receivers.forEach((r: InputListener) => {
            r.onKeyReleased(keyReleased)
          })
        case InputProcessorQueue.KEY_TYPED =>
          val character: Char = queue.remove(0).intValue.toChar
          receivers.forEach((r: InputListener) => {
            r.onKeyTyped(character)
          })
        case InputProcessorQueue.MOUSE_BUTTON_PRESSED =>
          val pressedX: Int = queue.remove(0)
          val pressedY: Int = queue.remove(0)
          val pressedButton: Int = queue.remove(0)
          receivers.forEach((r: InputListener) => {
            r.onMousePressed(pressedX, pressedY, pressedButton)
          })
        case InputProcessorQueue.MOUSE_BUTTON_RELEASED =>
          val releasedX: Int = queue.remove(0)
          val releasedY: Int = queue.remove(0)
          val releasedButton: Int = queue.remove(0)
          receivers.forEach((r: InputListener) => {
            r.onMouseReleased(releasedX, releasedY, releasedButton)
          })
        case InputProcessorQueue.MOUSE_MOVED =>
          val moveX: Int = queue.remove(0)
          val moveY: Int = queue.remove(0)
          receivers.forEach((r: InputListener) => {
            r.onMouseMoved(moveX, moveY)
          })
        case InputProcessorQueue.SCROLL =>
          val dir: Int = queue.remove(0)
          receivers.forEach((r: InputListener) => {
            r.onScroll(dir)
          })
        case _ =>
      }
    }
  }
}