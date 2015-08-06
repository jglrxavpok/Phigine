package org.jglr.phiengine.core.utils

import java.io._

object SystemUtils {

  private var baseFolder: File = null

  def getOS: OperatingSystem.Type = {
    val os: String = System.getProperty("os.name").toLowerCase
    if (os.contains("win")) {
      OperatingSystem.WINDOWS
    }
    else if (os.contains("sunos") || os.contains("solaris")) {
      OperatingSystem.SOLARIS
    }
    else if (os.contains("unix") || os.contains("linux")) {
      OperatingSystem.LINUX
    }
    else if (os.contains("mac")) {
      OperatingSystem.MACOSX
    } else {
      OperatingSystem.UNKNOWN
    }
  }

  def getUserName: String = {
    System.getProperty("user.name")
  }

  /**
   * Returns the folder where engine data is saved
   */
  def getBaseFolder(engineName: String): File = {
    if (baseFolder == null) {
      val appdata: String = System.getenv("APPDATA")
      if (appdata != null) baseFolder = new File(appdata, engineName)
      else baseFolder = new File(System.getProperty("user.home"), engineName)
      baseFolder.mkdirs
    }
    baseFolder
  }

  def deleteRecursively(file: File) {
    if (file.isDirectory) {
      val list: Array[File] = file.listFiles
      if (list != null) for (f <- list) {
        deleteRecursively(f)
        f.delete
      }
    }
    file.delete
  }

  def setBaseFolder(file: File) {
    baseFolder = file
  }
}