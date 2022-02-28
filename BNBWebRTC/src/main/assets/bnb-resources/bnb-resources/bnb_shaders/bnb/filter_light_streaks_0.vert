#include <bnb/glsl.vert>

BNB_LAYOUT_LOCATION(0)
BNB_IN vec2 attrib_pos;

#define PASS_ID 0

BNB_DECLARE_SAMPLER_2D(0, 1, s);

BNB_OUT(0)
vec4 var_uv_off;

void main()
{
    vec2 v = attrib_pos;
    gl_Position = vec4(v, 0., 1.);
    vec2 uv = v * 0.5 + 0.5;

    vec2 px_size = 1. / vec2(bnb_SCREEN.xy / 4.0);
    float kernel_scales[4];
    kernel_scales[0] = 1.;
    kernel_scales[1] = 1.3;
    kernel_scales[2] = 1.3 * 1.3;
    kernel_scales[3] = 1.3 * 1.3 * 1.3;
    var_uv_off = vec4(uv, px_size * kernel_scales[PASS_ID]);
#ifdef BNB_VK_1
    var_uv_off.y = 1. - var_uv_off.y;
#endif
}