package org.jglr.phiengine.core.maths

import java.nio.FloatBuffer

class Matrix(val rows: Int, val columns: Int, _data: Array[Float] = null) {

  protected val data = new Array[Float](columns*rows)

  if(_data != null) {
    for(i <- data.indices) {
      data(i) = _data(i)
    }
  }

  def copy: Matrix = {
    new Matrix(rows, columns, data)
  }

  def getSize = rows * columns

  def *(value: Float): Matrix = {
    val result = copy
    for(i <- data.indices) {
      result.data(i) *= value
    }
    result
  }

  def *=(value: Float): Matrix = {
    for(i <- data.indices) {
      data(i) *= value
    }
    this
  }

  def set(x: Int, y: Int, value: Float): Matrix = {
    data(y + x * rows) = value
    this
  }

  def get(x: Int, y: Int): Float = {
    data(y + x * rows)
  }

  def apply(x: Int, y: Int): Float = get(x,y)

  def update(x: Int, y: Int, value: Float) = set(x,y, value)

  def all(value: Float): Matrix = {
    for(i <- data.indices) {
      data(i) = value
    }
    this
  }

  def write(buffer: FloatBuffer) {
    for(j <- 0 until columns) {
      for(i <- 0 until rows) {
        buffer.put(get(i, j))
      }
    }
  }

  def identity: Matrix = {
    all(0)
    val max = Math.max(columns, rows)
    for(i <- 0 until max) {
      this(i, i) = 1
    }
    this
  }


  override def toString: String = {
    "mat"+rows+"x"+columns
  }

  def copyFrom(other: Matrix): Unit = {
    if(other.rows != rows || other.columns != columns) {
      throw new IllegalArgumentException("Cannot copy from different sized matrices (this: "+this.toString+", other: "+other.toString+")")
    }
    for(i <- 0 until rows) {
      for(j <- 0 until columns) {
        this(i, j) = other(i, j)
      }
    }
  }

  def transpose: Matrix = {
    val tmp = new Matrix(rows, columns)
    for(i <- 0 until rows) {
      for(j <- 0 until columns) {
        tmp(j, i) = this(i,j)
      }
    }
    copyFrom(tmp)
    this
  }
}
