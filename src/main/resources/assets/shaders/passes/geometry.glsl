#version 400
#include ../maths.glh
#ifdef __FRAGMENT__
in vec4 v_color;
in vec4 v_pos;
in vec2 v_texCoords;
in vec3 v_normal;
uniform sampler2D u_texture;
layout (location = 0) out vec4 diffuse;
layout (location = 1) out vec4 texCoords;
layout (location = 2) out vec4 worldPosition;
layout (location = 3) out vec4 normal;

void main()
{
    vec4 color = texture(u_texture, v_texCoords);
    diffuse = v_color * color;

    texCoords = vec4(0, v_texCoords.xy, 1);
    worldPosition = v_pos;
    normal = vec4(normalize(v_normal),1);
}
#endif

#ifdef __VERTEX__
#include ../defaultVertex.glh
#endif