package org.jglr.phiengine.client.render

import org.jglr.phiengine.client.render.g2d.Batch
import org.joml.{Vector2f, Vector3f}

object TemplateMeshes {

  def toFloats(vertices: Array[Vector3f], uvs: Array[Vector2f]): Array[Float] = {
    val arr = new Array[Float](vertices.length*Batch.vertexSize)
    for(i <- vertices.indices) {
      arr(i*Batch.vertexSize+0) = vertices(i).x
      arr(i*Batch.vertexSize+1) = vertices(i).y
      arr(i*Batch.vertexSize+2) = vertices(i).z

      arr(i*Batch.vertexSize+3) = uvs(i).x
      arr(i*Batch.vertexSize+4) = uvs(i).y

      arr(i*Batch.vertexSize+5) = 1
      arr(i*Batch.vertexSize+6) = 1
      arr(i*Batch.vertexSize+7) = 1
      arr(i*Batch.vertexSize+8) = 1
    }
    arr
  }

  def buildBox(w: Float, h: Float, d: Float): Mesh = {
    val halfw = w/2f
    val halfh = h/2f
    val halfd = d/2f

    val vertices = new Array[Vector3f](8)
    val uvs = new Array[Vector2f](8)
    val indices = new Array[Int](3*2*8)

    vertices(0) = new Vector3f(-halfw, -halfh, -halfd)
    vertices(1) = new Vector3f(halfw, -halfh, -halfd)
    vertices(2) = new Vector3f(halfw, halfh, -halfd)
    vertices(3) = new Vector3f(-halfw, halfh, -halfd)

    vertices(4) = new Vector3f(-halfw, -halfh, halfd)
    vertices(5) = new Vector3f(halfw, -halfh, halfd)
    vertices(6) = new Vector3f(halfw, halfh, halfd)
    vertices(7) = new Vector3f(-halfw, halfh, halfd)

    uvs(0) = new Vector2f(0,0)
    uvs(1) = new Vector2f(1,0)
    uvs(2) = new Vector2f(1,1)
    uvs(3) = new Vector2f(0,1)

    uvs(4) = new Vector2f(1,1)
    uvs(5) = new Vector2f(0,1)
    uvs(6) = new Vector2f(0,0)
    uvs(7) = new Vector2f(1,0)

    var faceOffset = 0
    // NORTH face
    indices(faceOffset+0) = 0
    indices(faceOffset+1) = 1
    indices(faceOffset+2) = 2

    indices(faceOffset+5) = 0
    indices(faceOffset+3) = 2
    indices(faceOffset+4) = 3
    faceOffset+=6

    // SOUTH face
    indices(faceOffset+0) = 4
    indices(faceOffset+1) = 5
    indices(faceOffset+2) = 6

    indices(faceOffset+3) = 4
    indices(faceOffset+4) = 6
    indices(faceOffset+5) = 7
    faceOffset+=6

    // TOP face
    indices(faceOffset+0) = 3
    indices(faceOffset+1) = 2
    indices(faceOffset+2) = 7

    indices(faceOffset+3) = 2
    indices(faceOffset+4) = 6
    indices(faceOffset+5) = 7
    faceOffset+=6

    // BOTTOM face
    indices(faceOffset+0) = 0
    indices(faceOffset+1) = 1
    indices(faceOffset+2) = 4

    indices(faceOffset+3) = 1
    indices(faceOffset+4) = 5
    indices(faceOffset+5) = 4
    faceOffset+=6


    val mesh: Mesh = new Mesh(8, indices.length)
    mesh.updateVertices(toFloats(vertices, uvs), vertices.length*Batch.vertexSize)
    mesh.updateIndices(indices, faceOffset)
    mesh
  }

  def buildPlane(w: Float, h: Float, d: Float): Mesh = {
    val halfw = w/2f
    val halfh = h/2f
    val halfd = d/2f

    val vertices = new Array[Vector3f](4)
    val uvs = new Array[Vector2f](4)
    val indices = new Array[Int](3*2*4)

    vertices(0) = new Vector3f(-halfw, halfh, -halfd)
    vertices(1) = new Vector3f(halfw, -halfh, -halfd)
    vertices(2) = new Vector3f(halfw, -halfh, halfd)
    vertices(3) = new Vector3f(-halfw, halfh, halfd)

    uvs(0) = new Vector2f(1,1)
    uvs(1) = new Vector2f(0,1)
    uvs(2) = new Vector2f(0,0)
    uvs(3) = new Vector2f(1,0)

    indices(0) = 0
    indices(1) = 1
    indices(2) = 2

    indices(3) = 2
    indices(4) = 3
    indices(5) = 0

    val mesh: Mesh = new Mesh(4, indices.length)
    mesh.updateVertices(toFloats(vertices, uvs), vertices.length*Batch.vertexSize)
    mesh.updateIndices(indices, indices.length)
    mesh
  }

  var cube: Mesh = buildBox(1f, 1f, 1f)

  var plane: Mesh = buildPlane(1f, 0f, 1f)
  
  var line: Mesh = buildPlane(0f,0.01f,1f)
}