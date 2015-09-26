package org.jglr.phiengine.core.utils

import java.io._
import java.util
import java.util._

import com.codedisaster.steamworks.{SteamException, SteamAPI}
import org.jglr.phiengine.client.utils.NativesSetup
import org.jglr.phiengine.core.utils.OperatingSystem.Type
import org.slf4j._

/**
 * Setups Steamworks4j natives for use of the SteamAPI.
 */
object SteamNativesSetup extends NativesSetup {
  override protected def postLoad(folder: File, logger: Logger): Unit = {
    if(!SteamAPI.init(folder.getAbsolutePath)) {
      val steamAppFile = new File(".", "steam_appid.txt")
      if(!steamAppFile.exists()) {
        throw new SteamException("Cannot load Steam because steam_appid.txt is not found :c")
      }
      throw new SteamException("Cannot load libraries")
    }
  }

  override protected def createNativesMap: util.Map[Type, Array[String]] = {
    val nativesMap: Map[OperatingSystem.Type, Array[String]] = new util.HashMap[OperatingSystem.Type, Array[String]]()

    val win: Array[String] = Array[String]("steam_api.dll", "steamworks4j.dll")
    nativesMap.put(OperatingSystem.WINDOWS, win)

    val macosx: Array[String] = Array[String]("libsteam_api.dylib", "libsteamworks4j.dylib")
    nativesMap.put(OperatingSystem.MACOSX, macosx)

    val unix: Array[String] = Array[String]("libsteam_api.so", "libsteamworks4j.so")
    nativesMap.put(OperatingSystem.LINUX, unix)
    nativesMap.put(OperatingSystem.SOLARIS, unix)

    nativesMap
  }
}