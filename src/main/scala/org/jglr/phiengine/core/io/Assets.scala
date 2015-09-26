package org.jglr.phiengine.core.io

import java.util
import com.google.gson.{JsonObject, Gson}
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.game.Game
import org.jglr.phiengine.core.utils.IOUtils

import scala.collection.JavaConversions._

class Assets(engine: PhiEngine, game: Game) {
  val file = new FilePointer(s"${game.id}/assets.json")
  val preload = new util.HashMap[String, util.List[FilePointer]]

  def load(): Unit = {
    val gson: Gson = new Gson()
    preload.put("textures", new util.ArrayList[FilePointer]())
    preload.put("shaders", new util.ArrayList[FilePointer]())
    if(file.exists) {
      val content = file.strReadAll
      val data = gson.fromJson(content, classOf[JsonObject])
      if(data.has("preload")) {
        val preloadingData = data.getAsJsonObject("preload")
        preload(preloadingData, "textures")
        preload(preloadingData, "shaders")
      }
    }
  }

  def preload(data: JsonObject, dataType: String): Unit = {
    if(data.has(dataType)) {
      val list = data.getAsJsonArray(dataType)
      for(s <- list) {
        val pointer = convertToPointer(s.getAsString)
        preload.get(dataType).add(pointer)
      }
    }
  }

  private def convertToPointer(txt: String): FilePointer = {
    val colonIndex = txt.indexOf(':')
    val fileType =
      if(colonIndex < 0) {
        FileType.CLASSPATH
      }
      else {
        FileType.withName(txt.substring(0, colonIndex).toUpperCase)
      }
    val path =
      if(colonIndex < 0) {
        txt
      } else {
        txt.substring(colonIndex+1)
      }
    new FilePointer(path, fileType)
  }
}
