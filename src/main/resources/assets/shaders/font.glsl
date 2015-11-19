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
#include defaultVertex.glh
#endif