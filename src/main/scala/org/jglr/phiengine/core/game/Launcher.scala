package org.jglr.phiengine.core.game

import java.util.function.Consumer

import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.utils.{JavaConversions, PhiConfig}

object LauncherUtils {
  def getWidthFromRatio(height: Int, ratio: Float): Int = (height*ratio).toInt
}

class Launcher(val gameClass: Class[_<:Game], f: PhiConfig => Unit = config => {}) extends App {
  val config = new PhiConfig
  f(config)

  if(args != null) {
    var currentArg: String = null
    var value: String = null
    for (s <- args) {
      if(s.startsWith("--")) {
        if(currentArg != null) {
          config.userDefined.put(currentArg, value)
        }
        currentArg = s.substring(2)
        value = "true"
      } else {
        value = s
      }
    }
    if(currentArg != null) {
      config.userDefined.put(currentArg, value)
    }
  }
  PhiEngine.start(gameClass, config)

  def this(gameClass: Class[_<:Game], func: java.util.function.Function[PhiConfig, Unit]) {
    this(gameClass, JavaConversions.toScalaFunction(func))
  }
}
