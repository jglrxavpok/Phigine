#version 400
#include maths.glh
#ifdef __FRAGMENT__
in vec4 v_color;
in vec4 v_pos;
in vec2 v_texCoords;
uniform sampler2D u_texture;
out vec4 finalColor;

void main()
{
    vec4 color = texture(u_texture, v_texCoords);
    finalColor = v_color * color;
}
#endif

#ifdef __VERTEX__
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_texCoord0;
layout(location = 2) in vec4 a_color;
uniform mat4 u_projection;
uniform mat4 u_modelview;
out vec4 v_color;
out vec2 v_texCoords;
out vec4 v_pos;

void main()
{
   v_color = a_color;
   v_texCoords = a_texCoord0;
   v_pos = u_projection * u_modelview * vec4(a_position, 1.0);
   gl_Position = v_pos;
}
#endif