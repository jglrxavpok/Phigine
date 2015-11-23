#version 400
#ifdef __FRAGMENT__
#include ../lighting.glh
in vec4 v_color;
in vec4 v_pos;
in vec2 v_texCoords;
in vec4 v_normal;
uniform sampler2D u_diffuse;
uniform sampler2D u_positions;
uniform sampler2D u_normals;
uniform float u_specularIntensity = 1;
uniform float u_specularPower = 1;
uniform vec3 u_eyeWorldPos;
uniform vec2 u_screenSize;
uniform PointLight u_pointLight;
layout (location = 0) out vec4 diffuse;
layout (location = 1) out vec4 texCoords;
layout (location = 2) out vec4 worldPosition;
layout (location = 3) out vec4 normal;

vec2 calcTexCoord()
{
   return gl_FragCoord.xy / u_screenSize;
}

void main()
{
   	vec2 texCoord = calcTexCoord();
   	vec3 worldPos = texture(u_positions, texCoord).xyz;
   	vec3 color = texture(u_diffuse, v_texCoords).xyz;
   	vec3 normal = texture(u_normals, v_texCoords).xyz;
   	normal = normalize(normal);

   	diffuse = vec4(color, 1.0) * calcPointLight(worldPos, normal, u_eyeWorldPos, u_specularPower, u_specularIntensity, u_pointLight);
}
#endif

#ifdef __VERTEX__
layout (location = 0) in vec3 a_pos;

uniform mat4 u_modelview;
uniform mat4 u_projection;

void main()
{
   	gl_Position = u_projection * u_modelview * vec4(a_pos, 1.0);
}
#endif