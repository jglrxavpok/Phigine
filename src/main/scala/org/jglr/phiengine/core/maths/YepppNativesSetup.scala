package org.jglr.phiengine.core.maths

import java.io.File
import java.util
import java.util.Map

import org.jglr.phiengine.core.utils.{OperatingSystem, NativesSetup}
import org.jglr.phiengine.core.utils.OperatingSystem.Type
import org.slf4j.Logger

object YepppNativesSetup  extends NativesSetup {
  override protected def createNativesMap: util.Map[Type, Array[String]] = {
    val nativesMap: Map[OperatingSystem.Type, Array[String]] = new util.HashMap[OperatingSystem.Type, Array[String]]()
    nativesMap
  }

  override protected def postLoad(folder: File, logger: Logger): Unit = {
    logger.info("Loaded Yeppp! "+info.yeppp.Library.getVersion) // Library::getVersion forces the load of the native files
  }

}
