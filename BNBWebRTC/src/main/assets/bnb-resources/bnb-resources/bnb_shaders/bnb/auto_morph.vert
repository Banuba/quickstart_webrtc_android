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

vec2 glfx_morph_coord(vec3 v)
{
    const float half_angle = radians(104.);
    const float y0 = -110.;
    const float y1 = 112.;
    float x = atan(v.x, v.z) / half_angle;
    float y = ((v.y - y0) / (y1 - y0)) * 2. - 1.;
    return vec2(x, y);
}

void main()
{
    gl_Position = vec4(glfx_morph_coord(attrib_static_pos), 0., 1.);
    translation = attrib_pos - attrib_static_pos;
}