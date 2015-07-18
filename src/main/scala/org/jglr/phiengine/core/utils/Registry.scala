package org.jglr.phiengine.core.utils

import com.google.common.collect.Maps
import java.util.HashMap
import java.util.Map
import java.util.Set
import java.util.function.Consumer
import scala.collection.JavaConversions._

class Registry[Key >: Null, Value] {
  private final val map: Map[Key, Value] = new HashMap[Key, Value]

  def register(key: Key, value: Value) {
    map.put(key, value)
  }

  def get(key: Key): Value = {
    map.get(key)
  }

  def foreachValue(action: Consumer[_ >: Value]) {
    map.values.forEach(action)
  }

  def foreachKey(action: Consumer[_ >: Key]) {
    map.keySet.forEach(action)
  }

  def has(key: Key): Boolean = {
    map.containsKey(key)
  }

  def keySet: Set[Key] = {
    map.keySet
  }

  def remove(s: Key) {
    map.remove(s)
  }

  def findKey(value: Value): Key = {
    for (e <- map.entrySet) {
      if (e.getValue == value) {
        return e.getKey
      }
    }
    null
  }

  def clear() {
    map.clear()
  }
}