package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render._

/**
 * Batch specialized in sprite rendering (aka. textured quads)
 * @param spriteLimit
 *                    The maximum number of sprites in one rendering
 */
class SpriteBatch(val spriteLimit: Int = 100) extends Batch(spriteLimit * Batch.vertexSize * 4, spriteLimit * 3 * 2,
  defaultShader = new Shader("assets/shaders/sprites.glsl")) {

  private var currentText: ITexture = null
  private var spriteCount: Int = 0

  def getSpriteLimit: Int = {
    spriteLimit
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

  /**
   * Draws a texture in the batch.
   * @param texture
   *                The texture to draw
   * @param x
   *          The x coordinate of the top left vertex of the texture
   * @param y
   *          The y coordinate of the top left vertex of the texture
   * @param z
   *          The z coordinate of the top left vertex of the texture
   */
  def draw(texture: ITexture, x: Float, y: Float, z: Float = 0f) {
    val w: Float = texture.getWidth
    val h: Float = texture.getHeight
    draw(texture, x, y, z, w, h)
  }

  /**
   * Draws a texture region in the batch.
   * @param region
   *                The texture region to draw
   * @param x
   *          The x coordinate of the top left vertex of the region
   * @param y
   *          The y coordinate of the top left vertex of the region
   * @param z
   *          The z coordinate of the top left vertex of the region
   * @param w
   *          The width of the texture region
   * @param h
   *          The height of the texture region
   * @param color
   *              The color in which to draw the region, use `Colors.white` if you want to keep the default aspect
   */
  def draw(region: TextureRegion, x: Float, y: Float, z: Float, w: Float, h: Float, color: Color) {
    addVertex(x, y, z, region.getMinU, region.getMaxV, color)
    addVertex(x + w, y, z, region.getMaxU, region.getMaxV, color)
    addVertex(x + w, y + h, z, region.getMaxU, region.getMinV, color)
    addVertex(x, y + h, z, region.getMinU, region.getMinV, color)
    addIndex(1)
    addIndex(0)
    addIndex(2)

    addIndex(2)
    addIndex(0)
    addIndex(3)
    nextSprite()
  }

  /**
   * Draws a texture in the batch.
   * @param texture
   *                The texture to draw
   * @param x
   *          The x coordinate of the top left vertex of the texture
   * @param y
   *          The y coordinate of the top left vertex of the texture
   * @param z
   *          The z coordinate of the top left vertex of the texture
   * @param w
   *          The width of the texture
   * @param h
   *          The height of the texture
   */
  def draw(texture: ITexture, x: Float, y: Float, z: Float, w: Float, h: Float) {
    setTexture(texture)
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

  /**
   * Prepares the batch to render another sprite, flushes if reaching spriteLimit
   */
  def nextSprite() {
    addToCursor(4)
    spriteCount += 1
    if (indexOffset >= indices.length) {
      flush()
    }
  }

  /**
   * Sets the texture used by the batch, flushes before setting it if the current one is different from `texture`
   * @param texture
   *                The texture to set
   */
  def setTexture(texture: ITexture) {
    if (texture != currentText) {
      flush()
      this.currentText = texture
      currentText.bind()
    }
  }

}