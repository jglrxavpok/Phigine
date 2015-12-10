package org.jglr.phiengine.core.io

import org.jglr.phiengine.core.utils.StringUtils
import java.io._

object FilePointer {
  implicit def toFilePointer(_path: String): FilePointer = {
    convertToPointer(_path)
  }

  def convertToPointer(txt: String): FilePointer = {
    val colonIndex = txt.indexOf(':')
    val fileType =
      if(colonIndex < 0) {
        FileType.CLASSPATH
      }
      else {
        FileType.withName(txt.substring(0, colonIndex).toUpperCase)
      }
    val path =
      if(colonIndex < 0) {
        txt
      } else {
        txt.substring(colonIndex+1)
      }
    new FilePointer(path, fileType)
  }
}

class FilePointer(val _path: String, val fileType: FileType.Type = FileType.CLASSPATH) extends Comparable[FilePointer] {
  private final val path: String = _path.replace("\\", "/")
  val isAbsolute = if(path.isEmpty) false else path.charAt(0) == '/'

  @throws(classOf[IOException])
  def createInputStream: InputStream = {
    fileType match {
      case FileType.CLASSPATH =>
        classOf[FilePointer].getResourceAsStream("/" + path)
      case FileType.DISK =>
        new FileInputStream(path)
      case FileType.VIRTUAL =>
        new ByteArrayInputStream(new Array[Byte](0))
      case _ =>
        throw new UnsupportedOperationException("Unknown type: " + getType)
    }
  }

  @throws(classOf[IOException])
  def readAll: Array[Byte] = {
    if (!exists) {
      throw new FileNotFoundException("File " + this + " not found")
    }
    val baos: ByteArrayOutputStream = new ByteArrayOutputStream
    val stream: InputStream = new BufferedInputStream(createInputStream)
    try {
      val buffer: Array[Byte] = new Array[Byte](1024 * 8)
      var i: Int = 0
      var read: Boolean = true
      while (read) {
        i = stream.read(buffer)
        if(i == -1) {
          read = false
        } else
          baos.write(buffer, 0, i)
      }
      baos.flush()
    } finally {
      if (stream != null) stream.close()
    }
    baos.toByteArray
  }

  override def toString: String = {
    path + "(" + fileType + ")"
  }

  def getPath: String = {
    path
  }

  def getType: FileType.Type = {
    fileType
  }

  override def equals(o: Any): Boolean = {
    o match {
      case other: FilePointer =>
        (other.getPath == path) && other.getType == fileType

      case _ =>
        false
    }
  }

  def child(child: String): FilePointer = {
    new FilePointer(path + "/" + child, getType)
  }

  def relative(name: String): FilePointer = {
    val parts: Array[String] = getPath.split("/")
    if (getName.isEmpty) {
      parts(parts.length - 2) = name
      new FilePointer(StringUtils.sum(parts, "/", 0, parts.length - 1), getType)
    }
    else {
      parts(parts.length - 1) = name
      new FilePointer(StringUtils.sum(parts, "/"), getType)
    }
  }

  def exists: Boolean = {
    fileType match {
      case FileType.CLASSPATH =>
        classOf[FilePointer].getResourceAsStream("/" + path) != null
      case FileType.DISK =>
        new File(path).exists
      case FileType.VIRTUAL =>
        true
      case _ =>
        throw new UnsupportedOperationException("Unknown type: " + getType)
    }
  }

  def getName: String = {
    val parts: Array[String] = getPath.split("/")
    parts(parts.length - 1)
  }

  def getExtension: String = {
    val parts: Array[String] = getPath.split("/")
    val nameParts: Array[String] = parts(parts.length - 1).split("\\.")
    nameParts(nameParts.length - 1)
  }

  @throws(classOf[IOException])
  def strReadAll: String = {
    new String(readAll, "UTF-8")
  }

  override def hashCode: Int = {
    val BASE: Int = 17
    val MULTIPLIER: Int = 31
    var result: Int = BASE
    result = MULTIPLIER * result + path.hashCode
    result = MULTIPLIER * result + fileType.id
    result
  }

  override def compareTo(o: FilePointer): Int = toString.compareTo(o.toString)
}