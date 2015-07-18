import org.jglr.phiengine.client.input.InputProcessor

class TestInput(testGame: TestGame2) extends InputProcessor {

  override def onKeyPressed(keycode: Int): Boolean = {
    return false
  }

  override def onKeyReleased(keycode: Int): Boolean = {
    return false
  }

  override def onKeyTyped(character: Char): Boolean = {
    return false
  }

  override def onMousePressed(screenX: Int, screenY: Int, button: Int): Boolean = {
    return false
  }

  override def onMouseReleased(screenX: Int, screenY: Int, button: Int): Boolean = {
    return false
  }

  override def onMouseMoved(screenX: Int, screenY: Int): Boolean = {
    testGame.x = screenX
    testGame.y = screenY
    return false
  }

  override def onScroll(dir: Int): Boolean = {
    return false
  }
}