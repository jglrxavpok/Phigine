package org.jglr.phiengine.client.render.particle

import java.awt.image.BufferedImage
import java.util

import org.jglr.phiengine.client.render.{TextureRegion, TextureMap}

object ParticleTextureMap {


  private val textureMap = new TextureMap("assets/textures/particles/")

  private val regions = new util.HashMap[String, TextureRegion]
  private val default = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB)
  default.setRGB(0,0,0xFFFFFFFF)
  val defaultRegion = textureMap.generateIcon(default)

  def setParticleTexture(id: String, path: String): Unit = {
    regions.put(id, textureMap.generateIcon(path))
    if(textureMap.isCompiled) {
      textureMap.compile // recompile the texture map
    }
  }

  def getRegion(id: String): TextureRegion = {
    if(regions.containsKey(id))
    regions.get(id)
    else
    defaultRegion
  }

  def bindMap(): Unit = {
    if(!textureMap.isCompiled) {
      textureMap.compile
      textureMap.writeDebugTexture()
    }
    textureMap.bind()
  }

  def unbindMap(): Unit = {
    textureMap.unbind()
  }

}
