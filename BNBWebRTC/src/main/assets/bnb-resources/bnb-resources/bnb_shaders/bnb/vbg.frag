#include <bnb/glsl.frag>

#include <bnb/texture_bicubic.glsl>


BNB_IN(0)
vec2 var_uv;
BNB_IN(1)
vec2 var_bg_uv;
BNB_IN(2)
vec2 var_bg_mask_uv;

BNB_DECLARE_SAMPLER_2D(0, 1, s_segmentation_mask);
BNB_DECLARE_SAMPLER_2D(2, 3, s_bg_texture);
BNB_DECLARE_SAMPLER_2D(4, 5, s_src_texture);


void main()
{
    float mask = bnb_texture_bicubic(BNB_PASS_SAMPLER_ARGUMENT(s_segmentation_mask), var_bg_mask_uv).x;
    vec3 bg_color = BNB_TEXTURE_2D(BNB_SAMPLER_2D(s_src_texture), var_uv).rgb;
    vec2 uv = var_bg_uv;

#ifndef BNB_VK_1
    if (vbg_texture_size.z < 0.5)
        uv.y = 1. - uv.y;
#endif

    vec2 uv_coeff = step(vec2(0.0, 0.0), uv) - step(vec2(1., 1.), uv);

    vec4 bg_tex_color = BNB_TEXTURE_2D(BNB_SAMPLER_2D(s_bg_texture), uv);
    bg_tex_color.rgb *= uv_coeff.x * uv_coeff.y;

    bnb_FragColor = vec4(
        mix(bg_color, bg_tex_color.rgb, mask * bg_tex_color.a),
        clamp(vbg_transparency_factor.x * bg_tex_color.a, (1. - mask), 1.));
}
