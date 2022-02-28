#include <bnb/glsl.vert>

BNB_LAYOUT_LOCATION(0)
BNB_IN vec3 attrib_pos;
BNB_LAYOUT_LOCATION(1)
BNB_IN vec3 attrib_static_pos;
BNB_LAYOUT_LOCATION(2)
BNB_IN vec2 attrib_uv;
BNB_LAYOUT_LOCATION(3)
BNB_IN vec4 attrib_mask;

BNB_OUT(0)
vec3 translation;

void main()
{
    vec2 v = smoothstep(0., 1., attrib_uv) * 2. - 1.;
    gl_Position = vec4(v, 0., 1.);
    const float max_range = 40.; // morph translation will be clamped to [-max_range,+max_range]
    translation = ((attrib_static_pos - attrib_pos) / max_range) * 0.5 + 0.5;
}