package org.jglr.phiengine.core.utils

class PhiConfig {
  var autoUpdates: Boolean = false
  var title: String = "PhiEngine game"
  var centered: Boolean = true
  var fullscreen: Boolean = false
  var height: Int = 640
  var width: Int = ((16f / 9f) * height).toInt
  var resizable: Boolean = false
  var decorated: Boolean = true
}