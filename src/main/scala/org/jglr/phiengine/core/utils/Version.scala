package org.jglr.phiengine.core.utils

import io.netty.buffer.ByteBuf
import org.jglr.phiengine.core.utils.ArrayUtils._
import org.jglr.phiengine.network.utils.NetworkSerializable

object Version {
  def create(version: String): Version = {
    if(version.contains(".")) {
      val parts = version.split("\\.")
      val major = getOr(parts, 0, "0").toInt
      val minor = getOr(parts, 1, "0").toInt
      val patch = getOr(parts, 2, "0").toInt
      val build = getOr(parts, 3, "0").toInt
      new Version(major, minor, patch, build)
    } else {
      new Version(1,0,0,1,true)
    }
  }
}

class Version(private var major: Int, private var minor: Int, private var patch: Int, private var build: Int, private var indev: Boolean = false) extends NetworkSerializable {

  def this() {
    this(0,0,0,0,false)
  }

  def getMajor: Int = this.major

  def getMinor: Int = minor

  def getPatch: Int = patch

  def getBuild: Int = build

  def isIndev: Boolean = indev

  def isSuperiorTo(version: Version): Boolean = {
    if(major > version.major) {
      true
    } else if(major < version.major) {
      false
    } else {
      if(minor > version.minor) {
        true
      } else if(minor < version.minor) {
        false
      } else {
        if(patch > version.patch) {
          true
        } else if(patch < version.patch) {
          false
        } else {
          if(build > version.build) {
            true
          } else if(build < version.build) {
            false
          } else {
            false
          }
        }
      }
    }
  }

  def isInferiorTo(version: Version): Boolean = {
    !isSuperiorTo(version) && !equals(version)
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case null =>
        false

      case v: Version =>
        v.build == build && v.major == major && v.minor == minor && v.patch == patch && v.indev == indev

      case _ =>
        false
    }
  }

  override def toString: String = {
    var result = major+"."+minor+"."+patch+"."+build
    if(indev) {
      result+="-INDEV"
    }
    result
  }

  override def read(buf: ByteBuf): Unit = {
    major = buf.readInt()
    minor = buf.readInt()
    patch = buf.readInt()
    build = buf.readInt()
    indev = buf.readBoolean()
  }

  override def write(buf: ByteBuf): Unit = {
    buf.writeInt(major)
    buf.writeInt(minor)
    buf.writeInt(patch)
    buf.writeInt(build)
    buf.writeBoolean(indev)
  }
}
