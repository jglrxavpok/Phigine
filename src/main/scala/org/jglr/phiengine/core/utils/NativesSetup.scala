package org.jglr.phiengine.core.utils

import java.io.{File, FileOutputStream, IOException, InputStream}
import java.util.Map
import java.util.regex.Pattern

import org.slf4j.Logger

abstract class NativesSetup {
  private var loaded: Boolean = false

  def createFolder(nativesFolder: File) = {
    val sub = getSubfolderName
    val folder =
      if(sub != null && !sub.isEmpty)
        new File(nativesFolder, sub)
      else
        nativesFolder
    if(!folder.exists())
      folder.mkdirs()
    if(!folder.isDirectory)
      folder.mkdirs()
    folder
  }

  /**
   * Load natives in given folder
   */
  @throws(classOf[IOException])
  def load(nativesFolder: File, logger: Logger) {
    if (!loaded) {
      val folder = createFolder(nativesFolder)
      val nativesMap: Map[OperatingSystem.Type, Array[String]] = createNativesMap
      val arch: String = System.getProperty("os.arch")
      val is64bits: Boolean = !(arch == "x86")
      val os: OperatingSystem.Type = SystemUtils.getOS
      val arch64Variants: Array[String] = Array[String]("_64", "64", "")
      val nativesList: Array[String] = nativesMap.get(os)
      if (nativesList == null) {
        logger.error("OS " + os + " is not supported, sorry :(")
      }
      else {
        for (f <- nativesList) {
          for (variant <- arch64Variants) {
            val fileName: String = if (is64bits) f.replace("32", "") else f
            val parts: Array[String] = fileName.split(Pattern.quote("."))
            var name: String = sum(parts, 0, parts.length - 1)
            name += variant + "." + parts(parts.length - 1)
            if (exists(name)) {
              if (!new File(folder, name).exists) {
                extractFromClasspath(name, folder)
                logger.info("Successfully extracted native from classpath: " + name)
              }
            }
          }
        }
      }
      postLoad(folder, logger)
      loaded = true
    }
  }

  protected def postLoad(folder: File, logger: Logger): Unit

  private def sum(parts: Array[String], offset: Int, length: Int): String = {
    StringUtils.sum(parts, "", offset, length)
  }

  private def exists(fileName: String): Boolean = {
    val stream: InputStream = classOf[OperatingSystem.Type].getResourceAsStream("/" + fileName)
    if (stream != null) {
      try {
        stream.close()
      }
      catch {
        case e: IOException => {
          e.printStackTrace()
        }
      }
      return true
    }
    false
  }

  protected def createNativesMap: Map[OperatingSystem.Type, Array[String]]

  /**
   * Extract given file from classpath into given folder
   */
  @throws(classOf[IOException])
  private def extractFromClasspath(fileName: String, folder: File) {
    val out: FileOutputStream = new FileOutputStream(new File(folder, fileName))
    IOUtils.copy(classOf[OperatingSystem.Type].getResourceAsStream("/" + fileName), out)
    out.flush()
    out.close()
  }

  /**
   * If this does not return <code>null</code>, the setup will create a subfolder with the name returned inside the provided natives folder
   * @return
   *         The subfolder name, if any. Null otherwise
   */
  protected def getSubfolderName: String = null
}
