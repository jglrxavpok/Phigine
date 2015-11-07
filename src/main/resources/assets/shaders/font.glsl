#version 330

#ifdef __FRAGMENT__
in vec4 v_color;
in vec2 v_texCoords;
uniform sampler2D u_texture;
out vec4 finalColor;

void main()
{
    float color = texture2D(u_texture, v_texCoords).r;
    float pixel = color;
    float alpha = v_color.a;
    if(color <= 0.45) {
        pixel = 0;
        alpha = 0;
    } else {
        pixel = color;
    }
    vec3 base = vec3(pixel, pixel, pixel);
    base *= v_color.rgb;
    finalColor = vec4(base,alpha);
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