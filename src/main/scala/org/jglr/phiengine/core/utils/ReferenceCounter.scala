package org.jglr.phiengine.core.utils

abstract class ReferenceCounter {
  private var refCount: Int = 0

  def incRefCount() {
    refCount += 1
  }

  def decRefCount() {
    refCount -= 1
  }

  def isDisposable: Boolean = {
    refCount <= 0
  }
}