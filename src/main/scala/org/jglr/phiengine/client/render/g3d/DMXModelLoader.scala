package org.jglr.phiengine.client.render.g3d

import java.util

import org.jglr.dmx.Datamodel
import org.jglr.dmx.attributes.containers.{Vector2, Vector3}
import org.jglr.dmx.attributes.{Attribute, AttributeValue, EnumAttributeTypes}
import org.jglr.dmx.codecs.BinaryCodec
import org.jglr.phiengine.client.ClientEngineSettings
import org.jglr.phiengine.client.render.{Texture, Mesh}
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.utils.JavaConversions._
import org.joml.{Vector2f, Vector4f, Vector3f}
import org.lwjgl.opengl.GL11

import scala.collection.JavaConversions._

object DMXModelLoader extends ModelLoader {

  private val codec = new BinaryCodec
  private val dataCache = new util.HashMap[FilePointer, Datamodel]()
  private val meshCache = new util.HashMap[FilePointer, Mesh]()

  def reconstructUVs(indexArray: Array[Int], texCoords: util.ArrayList[Vector2f], indices: Array[Int], flipV: Boolean): util.List[Vector2f] = {
    val map = new util.HashMap[Int, Vector2f]()
    var index = 0
    val result = new util.ArrayList[Vector2f]()
    for(i <- indices) {
      val vec = texCoords(i)
      if(flipV)
        vec.set(vec.x, 1f-vec.y)
      map.put(indexArray(index), vec)
      index += 1
      result.add(vec)
    }
    map.keySet().stream().sorted().forEachOrdered((i: Int) => result.add(map.get(i)))
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
      if(!dataCache.containsKey(pointer)) {
        codec.decode(5, pointer.createInputStream)
      } else {
        dataCache.get(pointer)
      }

    dataCache.put(pointer, datamodel)

    val materialAttr = datamodel.getElementByClass("DmeMaterial")
    checkType(materialAttr.getAttribute("mtlName"), EnumAttributeTypes.STRING)

    val textureName = materialAttr.getAttribute("mtlName").getValue.getRawValue
    new Model(mesh, new Texture(new FilePointer("assets/textures/model/"+textureName.asInstanceOf[String])))
  }

  override def loadMesh(pointer: FilePointer): Mesh = {
    if(!ClientEngineSettings.disallowMeshCaching && meshCache.containsKey(pointer))
      return meshCache.get(pointer)

    val datamodel =
      if(!dataCache.containsKey(pointer)) {
        codec.decode(5, pointer.createInputStream)
      } else {
        dataCache.get(pointer)
      }

    dataCache.put(pointer, datamodel)
    val vertexData = datamodel.getElementByClass("DmeVertexData")
    val positionAttr = vertexData.getAttribute("positions")
    checkType(positionAttr, EnumAttributeTypes.VECTOR3_ARRAY)

    val posIndicesAttr = vertexData.getAttribute("positionsIndices")
    checkType(posIndicesAttr, EnumAttributeTypes.INT_ARRAY)

    val textureUVsAttr = vertexData.getAttribute("textureCoordinates")
    checkType(textureUVsAttr, EnumAttributeTypes.VECTOR2_ARRAY)

    val textureIndicesAttr = vertexData.getAttribute("textureCoordinatesIndices")
    checkType(textureIndicesAttr, EnumAttributeTypes.INT_ARRAY)

    val normalsAttr = vertexData.getAttribute("normals")
    checkType(normalsAttr, EnumAttributeTypes.VECTOR3_ARRAY)

    val normalsIndicesAttr = vertexData.getAttribute("normalsIndices")
    checkType(normalsIndicesAttr, EnumAttributeTypes.INT_ARRAY)

    val normals = normalsAttr.getValue.getRawValue.asInstanceOf[Array[Vector3]]
    val normalsIndices = normalsIndicesAttr.getValue.getRawValue.asInstanceOf[Array[Int]]

    val vertArray = positionAttr.getValue.getRawValue.asInstanceOf[Array[Vector3]]
    val texCoords = new util.ArrayList[Vector2f]()
    val texCoordsArray = textureUVsAttr.getValue.getRawValue.asInstanceOf[Array[Vector2]]
    val texIndices = textureIndicesAttr.getValue.getRawValue.asInstanceOf[Array[Int]]

    val verticesIndices = new util.ArrayList[Int]()

    val indexArray = posIndicesAttr.getValue.getRawValue.asInstanceOf[Array[Int]]
    // get draw indices from faces info
    val faceSet = datamodel.getElementByClass("DmeFaceSet").getAttribute("faces")
    checkType(faceSet, EnumAttributeTypes.INT_ARRAY)
    val faces = faceSet.getValue.getRawValue.asInstanceOf[Array[Int]]
    var i = 0
    var faceSize = -1

    var faceData: Array[Int] = null

    val flipVCoords = vertexData.getAttribute("flipVCoordinates")
    checkType(flipVCoords, EnumAttributeTypes.BOOL)

    val vertices = new util.ArrayList[Vertex]
    while(i < faces.length) {
      while(faces(i) != -1) {
        if(faceData != null) {
          faceData(i % faceSize) = faces(i)
        }
        i+=1
      }
      i+=1
      if(faceSize == -1) {
        faceSize = i
        faceData = new Array[Int](faceSize)
        i = 0
      } else {
        // triangulate the faces
        for(index <- 0 until faceData.length-3) {
          /* verticesIndices.add(indexArray(faceData(0)))
          verticesIndices.add(indexArray(faceData(index+1)))
          verticesIndices.add(indexArray(faceData(index+2)))

          texCoords.add(texCoordsArray(texIndices(faceData(0))))
          texCoords.add(texCoordsArray(texIndices(faceData(index+1))))
          texCoords.add(texCoordsArray(texIndices(faceData(index+2))))*/
          def toVertex(index: Int): Vertex = {
            val pos = vertArray(indexArray(faceData(index)))
            val uv = texCoordsArray(texIndices(faceData(index)))
            val normal = normals(normalsIndices(faceData(index)))
            val vertex = new Vertex
            vertex.pos = new Vector3f(pos.getX, pos.getY, pos.getZ)
            vertex.texCoord = new Vector2f()
            if(flipVCoords.getValue.getRawValue.asInstanceOf[Boolean]) {
              vertex.texCoord.set(uv.getX, uv.getY)
            } else {
              vertex.texCoord.set(uv.getX, 1f-uv.getY)
            }
            vertex.normal = new Vector3f(normal.getX, normal.getY, normal.getZ)
            vertex.color = new Vector4f(1,1,1,1)
            vertex
          }

          // TODO: Try to find already existing vertices instead of reinstantiate it
          vertices.add(toVertex(0))
          vertices.add(toVertex(index+1))
          vertices.add(toVertex(index+2))
          verticesIndices.add(vertices.size()-3)
          verticesIndices.add(vertices.size()-2)
          verticesIndices.add(vertices.size()-1)
        }
      }
    }


    val mesh: Mesh = (vertices, verticesIndices)
   // mesh.defaultDrawMode = GL11.GL_QUADS
    mesh
  }

  private def checkType(attrValue: Attribute, attrType: EnumAttributeTypes): Unit = {
    if(attrValue == null || attrValue.getValue == null) {
      if(attrType != EnumAttributeTypes.UNKNOWN)
        throw new IllegalStateException("Expected attribute to be of type "+attrType.name+", but actually is "+EnumAttributeTypes.UNKNOWN)
    }
    if(attrValue.getValue.getType != attrType) {
      throw new IllegalStateException("Expected attribute "+attrValue.getName+" to be of type "+attrType.name+", but actually is "+attrValue.getValue.getType)
    }
  }

  implicit def toVec2f(v: Vector2): Vector2f = {
    new Vector2f(v.getX, v.getY)
  }
}
