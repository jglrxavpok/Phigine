import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.game.Launcher
import org.jglr.phiengine.core.utils.PhiConfig

object TestLauncher extends Launcher(classOf[TestGame2],
  config =>
    config.usesSteamAPI = false
)