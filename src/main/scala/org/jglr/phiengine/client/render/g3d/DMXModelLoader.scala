package org.jglr.phiengine.client.render.g3d

import java.util

import org.jglr.dmx.Datamodel
import org.jglr.dmx.attributes.containers.{Vector2, Vector3}
import org.jglr.dmx.attributes.{Attribute, AttributeValue, EnumAttributeTypes}
import org.jglr.dmx.codecs.BinaryCodec
import org.jglr.phiengine.client.ClientEngineSettings
import org.jglr.phiengine.client.render.{Texture, Mesh}
import org.jglr.phiengine.core.io.FilePointer
import org.joml.{Vector2f, Vector4f, Vector3f}

import scala.collection.JavaConversions._

object DMXModelLoader extends ModelLoader {

  private val codec = new BinaryCodec
  private val datacache = new util.HashMap[FilePointer, Datamodel]()
  private val meshcache = new util.HashMap[FilePointer, Mesh]()

  def reconstructUVs(texCoords: util.ArrayList[Vector2f], indices: Array[Int]): util.List[Vector2f] = {
    val result = new util.ArrayList[Vector2f]()
    for(i <- indices) {
      result.add(texCoords(i))
    }
    result
  }

  override def loadModel(pointer: FilePointer): Model = {
    try {
      val mesh = loadMesh(pointer)
      createModel(mesh, pointer)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        null
    }
  }

  override def createModel(mesh: Mesh, pointer: FilePointer): Model = {
    val datamodel =
      if(!datacache.containsKey(pointer)) {
        codec.decode(5, pointer.createInputStream)
      } else {
        datacache.get(pointer)
      }

    datacache.put(pointer, datamodel)

    val materialAttr = datamodel.getElementByClass("DmeMaterial")
    checkType(materialAttr.getAttribute("mtlName"), EnumAttributeTypes.STRING)

    val textureName = materialAttr.getAttribute("mtlName").getValue.getRawValue
    new Model(mesh, new Texture(new FilePointer("assets/textures/model/"+textureName.asInstanceOf[String])))
  }

  override def loadMesh(pointer: FilePointer): Mesh = {
    if(!ClientEngineSettings.disallowMeshCaching && meshcache.containsKey(pointer))
      return meshcache.get(pointer)

    val datamodel =
      if(!datacache.containsKey(pointer)) {
        codec.decode(5, pointer.createInputStream)
      } else {
        datacache.get(pointer)
      }

    datacache.put(pointer, datamodel)
    val vertexData = datamodel.getElementByClass("DmeVertexData")
    val positionAttr = vertexData.getAttribute("positions")
    checkType(positionAttr, EnumAttributeTypes.VECTOR3_ARRAY)

    val posIndicesAttr = vertexData.getAttribute("positionsIndices")
    checkType(posIndicesAttr, EnumAttributeTypes.INT_ARRAY)

    val textureUVsAttr = vertexData.getAttribute("textureCoordinates")
    checkType(textureUVsAttr, EnumAttributeTypes.VECTOR2_ARRAY)

    val textureIndicesAttr = vertexData.getAttribute("textureCoordinatesIndices")
    checkType(textureIndicesAttr, EnumAttributeTypes.INT_ARRAY)

    val colors = new util.ArrayList[Vector4f]()
    val verticesPos = new util.ArrayList[Vector3f]()
    val vertArray = positionAttr.getValue.getRawValue.asInstanceOf[Array[Vector3]]
    for(i <- vertArray.indices) {
      colors.add(new Vector4f(1,1,1,1)) // white
    }

    for(vec <- vertArray) {
      verticesPos.add(new Vector3f(vec.getX, vec.getY, vec.getZ))
    }

    val verticesIndices = new util.ArrayList[Int]()
    val indexArray = posIndicesAttr.getValue.getRawValue.asInstanceOf[Array[Int]]
    for(i <- indexArray) {
      verticesIndices.add(i)
    }

    val texCoords = new util.ArrayList[Vector2f]()
    val texCoordsArray = textureUVsAttr.getValue.getRawValue.asInstanceOf[Array[Vector2]]
    for(uv <- texCoordsArray) {
      texCoords.add(new Vector2f(uv.getX, uv.getY))
    }

    (verticesPos, reconstructUVs(texCoords, textureIndicesAttr.getValue.getRawValue.asInstanceOf[Array[Int]]), colors, verticesIndices)
  }

  private def checkType(attrValue: Attribute, attrType: EnumAttributeTypes): Unit = {
    if(attrValue.getValue.getType != attrType) {
      throw new IllegalStateException("Expected attribute "+attrValue.getName+" to be of type "+attrType.name+", but actually is "+attrValue.getValue.getType)
    }
  }
}
