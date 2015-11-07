package org.jglr.phiengine.client.utils

import java.io._
import java.util
import java.util._
import java.util.regex._
import com.google.common.collect._
import org.jglr.phiengine.core.utils.OperatingSystem.Type
import org.jglr.phiengine.core.utils._
import org.slf4j._

object LWJGLSetup extends NativesSetup {
  override protected def postLoad(folder: File, logger: Logger): Unit = {
    System.setProperty("net.java.games.input.librarypath", folder.getAbsolutePath)
    System.setProperty("org.lwjgl.librarypath", folder.getAbsolutePath)
    logger.info("Setup LWJGL paths to "+folder.getAbsolutePath)
  }

  override protected def createNativesMap: util.Map[Type, Array[String]] = {
    val nativesMap: Map[OperatingSystem.Type, Array[String]] = new util.HashMap[OperatingSystem.Type, Array[String]]()

    val win: Array[String] = Array[String]("lwjgl32.dll", "OpenAL32.dll", "jinput-dx8.dll", "jinput-raw.dll", "jinput-wintab.dll")
    nativesMap.put(OperatingSystem.WINDOWS, win)

    val macosx: Array[String] = Array[String]("liblwjgl.dylib", "libopenal.dylib", "libjinput-osx.jnilib")
    nativesMap.put(OperatingSystem.MACOSX, macosx)

    val unix: Array[String] = Array[String]("liblwjgl32.so", "libopenal32.so", "libjinput-linux.so")
    nativesMap.put(OperatingSystem.LINUX, unix)
    nativesMap.put(OperatingSystem.SOLARIS, unix)

    nativesMap
  }

  /**
   * If this does not return <code>null</code>, the setup will create a subfolder with the name returned inside the provided natives folder
   * @return
   * The subfolder name, if any. Null otherwise
   */
  override protected def getSubfolderName: String = "lwjgl"
}