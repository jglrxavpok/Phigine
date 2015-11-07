import org.jglr.phiengine.core.game.Launcher

object TestLauncher extends Launcher(classOf[TestGame2],
  config =>
    config.usesSteamAPI = false
)