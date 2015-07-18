package org.jglr.phiengine.core.utils

import org.jglr.phiengine.core.utils.ArrayUtils._

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

class Version(val major: Int, val minor: Int, val patch: Int, val build: Int, val indev: Boolean = false) {
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
    !isSuperiorTo(version)
  }

  override def toString: String = {
    var result = major+"."+minor+"."+patch+"."+build
    if(indev) {
      result+="-INDEV"
    }
    result
  }
}
