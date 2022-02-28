#include <bnb/glsl.vert>

BNB_LAYOUT_LOCATION(0)
BNB_IN vec2 attrib_pos;

BNB_OUT(0)
vec2 var_uv;

void main()
{
    mat2 ori = mat2(bnb_camera_orientation.xy, bnb_camera_orientation.zw);

    vec2 v = attrib_pos;
    gl_Position = vec4(v, 0., 1.);

    var_uv = (ori * vec2(v.x * bnb_camera_scale.x, v.y)) * 0.5 + 0.5;
}