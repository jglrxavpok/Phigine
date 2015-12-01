#version 400
#include ../maths.glh
#ifdef __FRAGMENT__
in vec2 v_texCoords;
uniform sampler2D u_particleTexture;
layout(location = 0) out vec4 fragColor;
uniform vec4 u_particleColor;
uniform float u_particleOpacity;
uniform vec4 u_particleRegion; // vec4 giving the uv coordinates of the particles (order: minU, minV, maxU, maxV)

vec2 calculateCorrectCoords(vec2 coords, vec4 region) {
    vec2 minUV = region.xy;
    vec2 maxUV = region.zw;
    vec2 correctCoords = (maxUV-minUV)*coords+minUV;
    return correctCoords;
}

void main() {
    vec4 finalColor = texture2D(u_particleTexture, calculateCorrectCoords(v_texCoords, u_particleRegion))*u_particleColor;
    finalColor.a = u_particleOpacity * finalColor.a;
    fragColor = finalColor;
}
#endif

#ifdef __VERTEX__
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_texCoords;
out vec2 v_texCoords;
uniform mat4 u_projection;
uniform mat4 u_modelview;

void main()
{
   v_texCoords = a_texCoords;
   gl_Position = u_projection * u_modelview * vec4(a_position, 1.0);
}
#endif