package org.jglr.phiengine.client.render

import java.util

import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.io.FilePointer
import org.jglr.phiengine.core.utils.Registry
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import java.io.IOException
import java.nio.FloatBuffer
import java.util.{Map, List, HashMap, ArrayList}
import java.util.Set
import java.util.Stack
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL32._
import org.lwjgl.opengl.GL43._
import scala.collection.JavaConversions._
import org.jglr.phiengine.core.utils.JavaConversions._

object Shaders {
  val POS_INDEX: Int = 0
  val UV_INDEX: Int = 1
  val NORMAL_INDEX: Int = 2
  val COLOR_INDEX: Int = 3

  var outputList = false
  var shouldCache = false

  var cache = new util.HashMap[FilePointer, ShaderHandle]
}

class ShaderHandle(shader: FilePointer) {
  private val identityMat = new Matrix4f().identity()
  private val uniforms: util.List[Uniform] = new util.ArrayList
  private val locations: util.Map[String, Int] = new util.HashMap
  private val id: Int = glCreateProgram
  private val path: FilePointer = shader
  private val registry: Registry[String, String] = new Registry[String, String]

  val vertexID: Int = compile(shader, GL_VERTEX_SHADER)
  registry.clear()
  val fragID: Int = compile(shader, GL_FRAGMENT_SHADER)
  registry.clear()
  glAttachShader(id, vertexID)
  glAttachShader(id, fragID)
  glLinkProgram(id)
  bind()
  uniforms.forEach((u: Uniform) => {
    if (u.uniformType.equals("mat4")) {
      setUniformMat4(u.name, identityMat)
    }
  })
  unbind()
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
      } else if (u.name.equals("u_modelview")) {
        setUniformMat4(u.name, identityMat)
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
    val conditions: util.Stack[Boolean] = new util.Stack
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
        } else if (command.startsWith("include ") && shouldRead) {
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
    val keys: util.Set[String] = registry.keySet
    var result: String = line
    for (k <- keys) {
      result = result.replace(k, get(k))
    }
    result
  }

  def setUniformMat4(name: String, m: Matrix4f) {
    val loc: Int = getLocation(name)
    val buffer: FloatBuffer = BufferUtils.createFloatBuffer(16)
    m.get(buffer).position(16).flip()
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

  def setUniform2f(name: String, x: Float, y: Float) {
    val loc: Int = getLocation(name)
    if (loc != -1) glUniform2f(loc, x, y)
  }

  def setUniform3f(name: String, x: Float, y: Float, z: Float) {
    val loc: Int = getLocation(name)
    if (loc != -1) glUniform3f(loc, x, y, z)
  }

  def setUniform4f(name: String, x: Float, y: Float, z: Float, w: Float) {
    val loc: Int = getLocation(name)
    if (loc != -1) glUniform4f(loc, x, y, z, w)
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

  private var previous: Int = 0

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

  def setUniform2f(name: String, x: Float, y: Float) {
    handle.setUniform2f(name, x, y)
  }

  def setUniform3f(name: String, x: Float, y: Float, z: Float) {
    handle.setUniform3f(name, x, y, z)
  }

  def setUniform4f(name: String, x: Float, y: Float, z: Float, a: Float) {
    handle.setUniform4f(name, x, y, z, a)
  }

  def setUniformMat4(name: String, m: Matrix4f) {
    handle.setUniformMat4(name, m)
  }

  def bind(): Unit = {
    previous = glGetInteger(GL_CURRENT_PROGRAM)
    handle.bind()
  }

  def update(): Unit = {
    handle.update()
  }

  def unbind() {
    glUseProgram(previous)
  }
}

class Uniform {
  var name: String = null
  var uniformType: String = null
}