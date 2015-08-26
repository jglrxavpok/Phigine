package org.jglr.phiengine.core.utils

import java.nio._

import org.lwjgl.BufferUtils

object Buffers {
  implicit def wrapByte(raw: (Array[Byte], Int)): ByteBuffer = {
    val elems = raw._1
    val buffer = BufferUtils.createByteBuffer(elems.length)
    for(i <- 0 until raw._2)
      buffer.put(elems(i))
    buffer.flip
    buffer
  }

  implicit def wrapFloat(raw: (Array[Float], Int)): FloatBuffer = {
    val elems = raw._1
    val buffer = BufferUtils.createFloatBuffer(elems.length)
    for(i <- 0 until raw._2)
      buffer.put(elems(i))
    buffer.flip
    buffer
  }

  implicit def wrapInt(raw: (Array[Int], Int)): IntBuffer = {
    val elems = raw._1
    val buffer = BufferUtils.createIntBuffer(elems.length)
    for(i <- 0 until raw._2)
      buffer.put(elems(i))
    buffer.flip
    buffer
  }

  implicit def wrapInt(elems: Array[Int]): IntBuffer = {
    wrapInt((elems, elems.length))
  }

  implicit def wrapFloat(elems: Array[Float]): FloatBuffer = {
    wrapFloat((elems, elems.length))
  }

  implicit def wrapByte(elems: Array[Byte]): ByteBuffer = {
    wrapByte((elems, elems.length))
  }
}