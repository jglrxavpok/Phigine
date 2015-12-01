package org.jglr.phiengine.client.render

import java.awt._
import java.awt.image._
import java.io._
import java.util
import java.util._
import javax.imageio._
import com.google.common.collect._
//import com.google.gson._
import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.utils.{ImageUtils, ITickable, StringUtils}

import scala.collection.JavaConversions._

class TextureMap(var base: FilePointer, var forceResize: Boolean = false, putInCorner: Boolean = true) extends ITickable with ITexture {
  private var texture: Texture = null
  private var nullImage: BufferedImage = null
  private var emptyImage: BufferedImage = null
  private var stitcher: Stitcher = null
  private var registredSprites: util.List[TextureMapSprite] = null
  private var stitchedImage: BufferedImage = null

  registredSprites = new util.ArrayList[TextureMapSprite]
  initNullAndEmptyImages()
  stitcher = new Stitcher(emptyImage, putInCorner)

  /**
   * Completes given FilePointer to get full FilePointer from base
   */
  def completeLocation(loc: FilePointer): FilePointer = {
    if(loc.isAbsolute)
      loc
    else
      new FilePointer(base.getPath + loc.getPath)
  }

  /**
   * Instantiates nullImage and emptyImage
   */
  private def initNullAndEmptyImages(): Unit = {
    if (completeLocation(new FilePointer("missigno.png")).exists) {
      try {
        nullImage = ImageUtils.loadImage(completeLocation(new FilePointer("missigno.png")))
      }
      catch {
        case e: Exception => {
          e.printStackTrace()
        }
      }
    }
    else {
      nullImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
      val g: Graphics = nullImage.createGraphics
      for(x <- 0 until 16) {
        for(y <- 0 until 16) {
          var color: Int = 0xFF000000
          if ((x >= 8 && y >= 8) || (x < 8 && y < 8))
            color = 0xFFFF00DC
          nullImage.setRGB(x, y, color)
        }
      }
      g.dispose()
    }
    if (completeLocation(new FilePointer(".png")).exists) {
      try {
        emptyImage = ImageUtils.loadImage(completeLocation(new FilePointer(".png")))
      }
      catch {
        case e: Exception => {
          e.printStackTrace()
        }
      }
    }
    else {
      emptyImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
      val g: Graphics = emptyImage.createGraphics
      for(x <- 0 until 16) {
        for(y <- 0 until 16) {
          if (x == 0 || y == 0)
            emptyImage.setRGB(x, y, 0xFF4800FF)
          else
            emptyImage.setRGB(x, y, 0xFFFF00DC)
        }
      }
      g.dispose()
    }
  }

  def generateIcon(loc: FilePointer): TextureRegion = {
    for (sprite <- registredSprites) {
      if (!sprite.useRawImage && (sprite.location == loc)) return sprite.icon
    }
    val metaLoc: FilePointer = new FilePointer(completeLocation(loc).getPath + ".json")
    val sprite: TextureMapSprite = new TextureMapSprite
    val icon: TextureMapIcon = new TextureMapIcon(0, 0, 0, 0, 0, 0)
    sprite.location = loc
    sprite.icon = icon
    registredSprites.add(sprite)
    return icon
  }

  /**
   * Generates an icon from given image
   */
  def generateIcon(img: BufferedImage): TextureRegion = {
    import scala.collection.JavaConversions._
    for (sprite <- registredSprites) {
      if (sprite.useRawImage && (sprite.rawImage == img)) return sprite.icon
    }
    val sprite: TextureMapSprite = new TextureMapSprite
    val icon: TextureMapIcon = new TextureMapIcon(0, 1, 0, 0, 0, 0)
    sprite.rawImage = img
    sprite.icon = icon
    sprite.useRawImage = true
    registredSprites.add(sprite)
    icon
  }

  /**
   * Compiles the TextureMap to create icons from given images
   */
  @throws(classOf[Exception])
  def compile {
    val indexes: HashMap[Integer, TextureRegion] = new HashMap[Integer, TextureRegion]
    for(i <- registredSprites.indices) {
      val sprite: TextureMapSprite = registredSprites.get(i)
      val icon: TextureRegion = sprite.icon
      var img: BufferedImage = null
      var name: String = null
      if (!sprite.useRawImage) {
        val loc: FilePointer = completeLocation(sprite.location)
        try {
          name = loc.getName
          img = ImageUtils.loadImage(loc)
        }
        catch {
          case e: Exception => {
            PhiEngine.getInstance.getLogger.error("Unable to find icon: /" + loc.getPath)
            img = nullImage
            icon.isNull = true
          }
        }
      }
      else {
        name = sprite.rawImage.toString
        img = sprite.rawImage
      }
      indexes.put(stitcher.addImage(img, name, forceResize), icon)
    }
    stitchedImage = stitcher.stitch
    val indexesIt: Iterator[Integer] = indexes.keySet.iterator
    while (indexesIt.hasNext) {
      val index: Int = indexesIt.next
      if (index >= 0) {
        val sprite: TextureMapSprite = registredSprites.get(index)
        val icon: TextureMapIcon = sprite.icon.asInstanceOf[TextureMapIcon]
        icon.setMinU(stitcher.getMinU(index))
        icon.setMinV(stitcher.getMinV(index))
        icon.setMaxU(stitcher.getMaxU(index))
        icon.setMaxV(stitcher.getMaxV(index))
        icon.setWidth(stitcher.getWidth(index))
        icon.setHeight(stitcher.getHeight(index))
      }
    }
    texture = Textures.createFrom(base.getPath, stitchedImage)
  }

  def writeDebugTexture(): Unit = {
    ImageIO.write(stitchedImage, "png", new File(".", base.getPath.replace("/", "__")+"_debug.png"))
  }

  def isCompiled: Boolean = texture != null

  /**
   * Gets a texture icon from given name or null if none exists with given name
   */
  def get(name: String): TextureRegion = {
    get(new FilePointer(name))
  }

  /**
   * Gets a texture icon from given location or null if none exists with given location
   */
  def get(loc: FilePointer): TextureRegion = {
    import scala.collection.JavaConversions._
    for (sprite <- registredSprites) {
      if (!sprite.useRawImage && sprite.location.equals(loc)) {
        return sprite.icon
      }
    }
    TextureRegion.NULL
  }

  def get(index: Int): TextureRegion = {
    registredSprites.get(index).icon
  }

  /**
   * Returns generated texture
   */
  def getTexture: Texture = {
    texture
  }

  def clear(): Unit = {
    registredSprites.clear()
  }

  /**
   * Sets tile size
   */
  def setTileDimensions(w: Int, h: Int) {
    stitcher.setTileWidth(w)
    stitcher.setTileHeight(h)
  }

  /**
   * Gets tile width
   */
  def getTileWidth: Int = {
    stitcher.getTileWidth
  }

  /**
   * Gets tile height
   */
  def getTileHeight: Int = {
    stitcher.getTileHeight
  }

  def generateIcon(loc: String): TextureRegion = {
    generateIcon(new FilePointer(loc + ".png"))
  }

  def bind(slot: Int = 0) {
    texture.bind(slot)
  }

  def dispose() {
    texture.dispose()
  }

  def tick(delta: Float) {
    for (sprite <- registredSprites) {
      sprite.tick(delta)
    }
  }

  def getSprite(textureIcon: TextureRegion): TextureMapSprite = {
    for (sprite <- registredSprites) {
      if (sprite.icon eq textureIcon) return sprite
    }
    null
  }

  private class TextureMapIcon(var minu: Float, var minv: Float, var maxu: Float, var maxv: Float, var width: Int, var height: Int)
    extends TextureRegion(minu, minv, maxu, maxv) {

    def getWidth: Float = {
      width
    }

    def getHeight: Float = {
      height
    }

    override def getMinU: Float = {
      minu
    }

    override def getMaxU: Float = {
      maxu
    }

    override def getMinV: Float = {
      minv
    }

    override def getMaxV: Float = {
      maxv
    }

    def setMinU(minu: Float) {
      this.minu = minu
    }

    def setMaxU(maxu: Float) {
      this.maxu = maxu
    }

    def setMinV(minv: Float) {
      this.minv = minv
    }

    def setMaxV(maxv: Float) {
      this.maxv = maxv
    }

    def setWidth(width: Int) {
      this.width = width
    }

    def setHeight(height: Int) {
      this.height = height
    }
  }

  override def unbind(): Unit = {
    texture.unbind()
  }

  override def getWidth: Int = {
    texture.getWidth
  }

  override def getHeight: Int = {
    texture.getHeight
  }
}