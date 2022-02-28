#include <bnb/glsl.vert>

BNB_LAYOUT_LOCATION(0)
BNB_IN vec3 attrib_pos;
BNB_LAYOUT_LOCATION(1)
BNB_IN vec2 attrib_uv;

BNB_OUT(0)
vec3 pos_static;

void main()
{
    vec2 v = attrib_uv * 2. - 1.;
    v.y = -v.y;
    gl_Position = vec4(v, 0., 1.);
    pos_static = attrib_pos;
}