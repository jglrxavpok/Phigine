import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.utils.PhiConfig

object TestLauncher extends App {

  val config: PhiConfig = new PhiConfig
  PhiEngine.start(classOf[TestGame2], config)
}
