package org.jglr.phiengine.client.ui.components

import org.jglr.phiengine.client.text.FontRenderer
import org.jglr.phiengine.client.ui.UIComponent

object DefaultListModel extends ListModel {
  override def createRepresentation(fontRenderer: FontRenderer, value: Any): UIComponent = {
    value match {
      case namedArgument: (String, Any) =>
        val name = namedArgument._1
        val arg = namedArgument._2
        arg match {
          case b: Boolean =>
            new UIToggleButton(fontRenderer, name, b)

          case func: (() => Unit) =>
            new UIFunctionButton(fontRenderer, name, func)

        }

      case number: Number =>
        new UILabel(fontRenderer, String.valueOf(number))

      case str: String =>
        new UILabel(fontRenderer, str)

      case _ =>
        new UILabel(fontRenderer, value.toString)
    }
  }
}
