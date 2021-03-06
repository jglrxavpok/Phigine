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
layout(location = 2) in vec3 a_normal;
layout(location = 3) in vec4 a_color;
uniform mat4 u_projection;
uniform mat4 u_modelview;
out vec4 v_color;
out vec4 v_normal;

void main()
{
   v_color = a_color;
   gl_Position = u_projection * u_modelview * vec4(a_position, 1.0);
   v_normal = u_modelview * vec4(a_normal, 1.0);
}
#endif