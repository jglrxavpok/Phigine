package org.jglr.phiengine.client.render.g3d

import org.jglr.phiengine.client.render.Mesh
import org.jglr.phiengine.client.render.g2d.Batch
import org.jglr.phiengine.core.io.FilePointer
import org.joml.{Vector2f, Vector4f, Vector3f}
import java.util

abstract class ModelLoader {

  def loadModel(pointer: FilePointer): Model

  def loadMesh(pointer: FilePointer): Mesh

  def createModel(mesh: Mesh, pointer: FilePointer): Model

  implicit def toMesh(meshData: (util.List[Vector3f] /*position*/, util.List[Vector2f]/*uv*/, util.List[Vector4f]/*color*/, util.List[Int]/*indices*/)): Mesh = {
    // convert position vectors to floats
    val indices = new Array[Int](meshData._4.size)
    for(i <- indices.indices) {
      indices(i) = meshData._4.get(i)
    }

    // convert position vectors to floats
    val vertices = new Array[Float](meshData._1.size*Batch.vertexSize)
    for(i <- 0 until meshData._1.size) {
      val vertex = meshData._1.get(i)
      val uv = meshData._2.get(i)
      val color = meshData._3.get(i)
      val vsize = Batch.vertexSize
      vertices(i*vsize) = vertex.x
      vertices(i*vsize+1) = vertex.y
      vertices(i*vsize+2) = vertex.z
      vertices(i*vsize+3) = uv.x
      vertices(i*vsize+4) = uv.y
      vertices(i*vsize+5) = color.x
      vertices(i*vsize+6) = color.y
      vertices(i*vsize+7) = color.z
      vertices(i*vsize+8) = color.w
    }

    val result = new Mesh(vertices.length, indices.length)
    result.updateIndices(indices, indices.length)
    result.updateVertices(vertices, vertices.length)
    result
  }

}
