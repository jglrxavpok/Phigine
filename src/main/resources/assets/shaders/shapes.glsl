#version 400
#include maths.glh
#ifdef __FRAGMENT__
in vec4 v_color;
out vec4 finalColor;

void main()
{
    finalColor = v_color;
}
#endif

#ifdef __VERTEX__
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec2 a_texCoord0;
layout(location = 2) in vec4 a_color;
uniform mat4 u_projection;
out vec4 v_color;

void main()
{
   v_color = a_color;
   gl_Position = u_projection * vec4(a_position, 1.0);
}
#endif