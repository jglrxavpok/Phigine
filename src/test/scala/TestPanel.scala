import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.UIComponent
import org.jglr.phiengine.client.ui.components.{UIButton, UIPanel}

class TestPanel(fontRenderer: FontRenderer) extends UIPanel(fontRenderer) {

  val button0 = new UIButton(fontRenderer, "Test0")
  val button1 = new UIButton(fontRenderer, "Test1")
  val button2 = new UIButton(fontRenderer, "Test2")
  addChild(button0)
  addChild(button1)
  addChild(button2)

  button0.previousComponent = button2
  button0.nextComponent = button1

  button1.previousComponent = button0
  button1.nextComponent = button2

  button2.previousComponent = button1
  button2.nextComponent = button0

  override def onComponentClicked(comp: UIComponent): Unit = {
    super.onComponentClicked(comp)
    println(s"clicked on $comp")
  }
}
