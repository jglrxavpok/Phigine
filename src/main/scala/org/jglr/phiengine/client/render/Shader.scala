package org.jglr.phiengine.client.render

import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.maths.Mat4
import org.jglr.phiengine.core.utils.Registry
import org.lwjgl.BufferUtils
import java.io.IOException
import java.nio.FloatBuffer
import java.util.{Map, List, HashMap, ArrayList}
import java.util.Set
import java.util.Stack
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL32._
import org.lwjgl.opengl.GL43._
import scala.collection.JavaConversions._
import org.jglr.phiengine.core.utils.JavaConversions._

object Shaders {
  val POS_INDEX: Int = 0
  val UV_INDEX: Int = 1
  val COLOR_INDEX: Int = 2

  var outputList = false
  var shouldCache = false

  var cache = new HashMap[FilePointer, ShaderHandle]
}

class ShaderHandle(shader: FilePointer) {
  private final val uniforms: List[Uniform] = new ArrayList
  private final val locations: Map[String, Int] = new HashMap
  private final val id: Int = glCreateProgram
  private final val path: FilePointer = shader
  private val registry: Registry[String, String] = new Registry[String, String]

  val vertexID: Int = compile(shader, GL_VERTEX_SHADER)
  registry.clear()
  val fragID: Int = compile(shader, GL_FRAGMENT_SHADER)
  registry.clear()
  glAttachShader(id, vertexID)
  glAttachShader(id, fragID)
  glLinkProgram(id)
  if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
    PhiEngine.getInstance.getLogger.error("Failed to link shader \n" + glGetProgramInfoLog(id))
  }

  def bind(): Unit = {
    glUseProgram(id)
    update()
  }

  def update(): Unit = {
    uniforms.forEach((u: Uniform) => {
      if (u.name.equals("u_projection")) {
        setUniformMat4(u.name, PhiEngine.getInstance.getProjectionMatrix)
      } else if (u.name.equals("u_time")) {
        setUniformd(u.name, PhiEngine.getInstance.getTime)
      }
    })
  }

  @throws(classOf[IOException])
  private def compile(file: FilePointer, shaderType: Int): Int = {
    var source: String = new String(file.readAll, "UTF-8")
    shaderType match {
      case GL_VERTEX_SHADER =>
        define("__VERTEX__", "1")
      case GL_FRAGMENT_SHADER =>
        define("__FRAGMENT__", "1")
      case GL_COMPUTE_SHADER =>
        define("__COMPUTE__", "1")
      case GL_GEOMETRY_SHADER =>
        define("__GEOMETRY__", "1")
    }
    source = preprocess(source)
    val id: Int = glCreateShader(shaderType)
    glShaderSource(id, source)
    glCompileShader(id)
    if (glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
      PhiEngine.getInstance.getLogger.error("Failed to load shader " + file + "\n" + glGetShaderInfoLog(id))
    }
    id
  }

  @throws(classOf[IOException])
  private def preprocess(source: String): String = {
    val builder: StringBuilder = new StringBuilder
    val lines: Array[String] = source.replace("\r", "").split("\n")
    val conditions: Stack[Boolean] = new Stack
    conditions.push(true)
    for (line <- lines) {
      val shouldRead: Boolean = conditions.peek
      if (line.startsWith("#")) {
        val command: String = line.substring(1)
        if (command.startsWith("version ")) {
          builder.append(line)
        } else if (command.startsWith("ifndef ") && shouldRead) {
          val arg: String = command.substring("ifndef ".length)
          conditions.push(!isDef(arg))
        } else if (command.startsWith("ifdef ") && shouldRead) {
          val arg: String = command.substring("ifdef ".length)
          conditions.push(isDef(arg))
        } else if (command.startsWith("define ") && shouldRead) {
          val index: Int = "define ".length
          var end: Int = command.indexOf(" ", "define ".length + 1)
          var arg: String = null
          var value: String = "1"
          if (end < 0) {
            end = command.length
            arg = command.substring(index, end)
          } else {
            arg = command.substring(index, end)
            value = command.substring(("define " + arg + " ").length)
          }
          define(arg, value)
        } else if (command.startsWith("endif")) {
          conditions.pop
        } else if (command.startsWith("include ")) {
          val arg: String = command.substring("include ".length)
          val toInclude: FilePointer = path.relative(arg)
          PhiEngine.getInstance.getLogger.info("Including shader file "+toInclude+" inside "+path)
          val content: String = preprocess(toInclude.strReadAll)
          builder.append(content)
        }
      } else if (line.startsWith("uniform ") && shouldRead) {
        val definition: String = line.substring("uniform ".length)
        val uniformType: String = definition.substring(0, definition.indexOf(" "))
        val index: Int = (uniformType + " ").length
        var endIndex: Int = definition.indexOf("=")
        if (endIndex < 0) {
          endIndex = definition.indexOf(";")
        }
        val name: String = definition.substring(index, endIndex)
        val uniform: Uniform = new Uniform
        uniform.uniformType = uniformType
        uniform.name = name
        uniforms.add(uniform)
        builder.append(replaceDefined(line))
      } else if (shouldRead) {
        builder.append(replaceDefined(line))
      }
      if (shouldRead) builder.append("\n")
    }
    builder.toString()
  }

  private def replaceDefined(line: String): String = {
    val keys: Set[String] = registry.keySet
    var result: String = line;
    for (k <- keys) {
      result = result.replace(k, get(k))
    }
    result
  }

  def setUniformMat4(name: String, m: Mat4) {
    val loc: Int = getLocation(name)
    val buffer: FloatBuffer = BufferUtils.createFloatBuffer(16)
    m.write(buffer)
    buffer.flip
    glUniformMatrix4fv(loc, false, buffer)
  }

  private def getLocation(name: String): Int = {
    var loc: Int = 0
    if (locations.containsKey(name)) {
      loc = locations.get(name)
    }
    else {
      loc = glGetUniformLocation(id, name)
      if (loc == -1) PhiEngine.getInstance.getLogger.error("Uniform with name '" + name + "' not found")
      locations.put(name, loc)
    }
    loc
  }

  def setUniformf(name: String, value: Float) {
    val loc: Int = getLocation(name)
    if (loc != -1) glUniform1f(loc, value)
  }

  def setUniformi(name: String, value: Int) {
    val loc: Int = getLocation(name)
    if (loc != -1) glUniform1i(loc, value)
  }

  def setUniformd(name: String, value: Double) {
    val loc: Int = getLocation(name)
    if (loc != -1) glUniform1f(loc, value.toFloat)
  }

  def unbind() {
    glUseProgram(0)
  }

  def define(arg: String, value: String) {
    registry.register(arg, value)
  }

  def get(arg: String): String = {
    registry.get(arg)
  }

  def isDef(arg: String): Boolean = {
    registry.has(arg)
  }

  def undefine(s: String) {
    registry.remove(s)
  }
}

@throws(classOf[IOException])
class Shader(shader: FilePointer) {
  val handle: ShaderHandle =
    if(Shaders.shouldCache) {
      if(Shaders.cache.containsKey(shader)) {
        Shaders.cache.get(shader)
      } else {
        val newHandle = new ShaderHandle(shader)
        Shaders.cache.put(shader, newHandle)
        newHandle
      }
    } else {
      new ShaderHandle(shader)
    }

  def setUniformf(name: String, value: Float) {
    handle.setUniformf(name, value)
  }

  def setUniformi(name: String, value: Int) {
    handle.setUniformi(name, value)
  }

  def setUniformd(name: String, value: Double) {
    handle.setUniformd(name, value)
  }

  def setUniformMat4(name: String, m: Mat4) {
    handle.setUniformMat4(name, m)
  }

  def bind(): Unit = {
    handle.bind()
  }

  def update(): Unit = {
    handle.update()
  }

  def unbind() {
    glUseProgram(0)
  }
}

class Uniform {
  var name: String = null
  var uniformType: String = null
}