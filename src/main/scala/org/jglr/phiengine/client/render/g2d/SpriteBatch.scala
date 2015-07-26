package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render._

class SpriteBatch(val spriteLimit: Int = 100) extends Batch(spriteLimit * Batch.vertexSize * 4, spriteLimit * 3 * 2,
  defaultShader = new Shader("assets/shaders/sprites.glsl")) {

  private var currentText: ITexture = null
  private var spriteCount: Int = 0

  def getSpriteLimit: Int = {
    spriteLimit
  }

  override def end(): Unit = {
    super.end()
  }

  override def flush(): Unit = {
    if (offset == 0) {
      return
    }
    if (shader != null) shader.bind()
    if (currentText != null) {
      currentText.bind()
    }
    mesh.updateVertices(verticesData, offset)
    mesh.updateIndices(indices, indexOffset)
    mesh.bind()
    mesh.render()
    reset()
  }

  def draw(texture: ITexture, x: Float, y: Float, z: Float = 0) {
    val w: Float = texture.getWidth()
    val h: Float = texture.getHeight()
    draw(texture, x, y, z, w, h)
  }

  def draw(region: TextureRegion, x: Float, y: Float, z: Float, w: Float, h: Float) {
    addVertex(x, y, z, region.getMinU, region.getMaxV, Colors.white)
    addVertex(x + w, y, z, region.getMaxU, region.getMaxV, Colors.white)
    addVertex(x + w, y + h, z, region.getMaxU, region.getMinV, Colors.white)
    addVertex(x, y + h, z, region.getMinU, region.getMinV, Colors.white)
    addIndex(1)
    addIndex(0)
    addIndex(2)

    addIndex(2)
    addIndex(0)
    addIndex(3)
    nextSprite()
  }

  def draw(texture: ITexture, x: Float, y: Float, z: Float, w: Float, h: Float) {
    if (currentText != texture) {
      flush()
      currentText = texture
      currentText.bind()
    }
    addVertex(x, y, z, 0, 1, Colors.white)
    addVertex(x + w, y, z, 1, 1, Colors.white)
    addVertex(x + w, y + h, z, 1, 0, Colors.white)
    addVertex(x, y + h, z, 0, 0, Colors.white)
    addIndex(1)
    addIndex(0)
    addIndex(2)

    addIndex(2)
    addIndex(0)
    addIndex(3)
    nextSprite()
  }

  def nextSprite() {
    addToCursor(4)
    spriteCount += 1
    if (indexOffset >= indices.length) {
      flush()
    }
  }

  def setTexture(texture: ITexture) {
    if (texture != currentText) {
      flush()
    }
    this.currentText = texture
  }

}