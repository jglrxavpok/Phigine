package org.jglr.phiengine.client.render.g3d

import org.jglr.phiengine.client.render.Camera
import org.jglr.phiengine.core.PhiEngine
import org.joml.Matrix4f

class PerspectiveCamera(fov: Float, aspect: Float, zNear: Float, zFar: Float) extends Camera(new Matrix4f().setPerspective(fov, aspect, zNear, zFar))
