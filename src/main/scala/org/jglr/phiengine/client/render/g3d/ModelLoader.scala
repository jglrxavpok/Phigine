package org.jglr.phiengine.client.render.g3d

import org.jglr.phiengine.client.render.{MeshUtils, Mesh}
import org.jglr.phiengine.client.render.g2d.Batch
import org.jglr.phiengine.core.io.FilePointer
import org.joml.{Vector2f, Vector4f, Vector3f}
import java.util

class Vertex {
  var pos: Vector3f = null
  var texCoord: Vector2f = null
  var normal: Vector3f = null
  var color: Vector4f = null
}

abstract class ModelLoader {

  def loadModel(pointer: FilePointer): Model

  def loadMesh(pointer: FilePointer): Mesh

  def createModel(mesh: Mesh, pointer: FilePointer): Model

  implicit def toMesh(meshData: (util.List[Vertex] /*position*/, util.List[Int]/*indices*/)): Mesh = {
    // convert position vectors to floats
    val indices = new Array[Int](meshData._2.size)
    val vertices = new Array[Float](meshData._1.size*MeshUtils.vertexSize)
    for(i <- indices.indices) {
      indices(i) = meshData._2.get(i)
    }

    // convert position vectors to floats
    for(i <- 0 until meshData._1.size) {
      val vertex = meshData._1.get(i)
      val pos = vertex.pos
      val uv = vertex.texCoord
      val normal = vertex.normal
      val color = vertex.color
      val vsize = MeshUtils.vertexSize
      vertices(i*vsize+0) = pos.x
      vertices(i*vsize+1) = pos.y
      vertices(i*vsize+2) = pos.z
      vertices(i*vsize+3) = uv.x
      vertices(i*vsize+4) = uv.y

      // normal
      vertices(i*vsize+5) = normal.x
      vertices(i*vsize+6) = normal.y
      vertices(i*vsize+7) = normal.z

      // color
      vertices(i*vsize+8) = color.x
      vertices(i*vsize+9) = color.y
      vertices(i*vsize+10) = color.z
      vertices(i*vsize+11) = color.w
    }

    val result = new Mesh(vertices.length, indices.length)
    result.updateIndices(indices, indices.length)
    result.updateVertices(vertices, vertices.length)
    result
  }

}
