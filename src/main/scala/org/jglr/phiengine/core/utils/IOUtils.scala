package org.jglr.phiengine.core.utils

import java.io._

object IOUtils {
  @throws(classOf[IOException])
  def copy(in: InputStream, out: OutputStream) {
    val buffer: Array[Byte] = new Array[Byte](4096)
    var i: Int = 0
    while (i != -1) {
      i = in.read(buffer)
      if(i != -1)
        out.write(buffer, 0, i)
    }
    out.flush()
  }
}