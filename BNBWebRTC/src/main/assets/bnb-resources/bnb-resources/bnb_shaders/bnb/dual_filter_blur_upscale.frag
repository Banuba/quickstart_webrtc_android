#include <bnb/glsl.frag>

BNB_DECLARE_SAMPLER_2D(0, 1, s_upscale_tex);

BNB_IN(0)
vec4 var_uv;

void main()
{
    // clang-format off
    vec3 sum =
        (1. / 6.) *
        (BNB_TEXTURE_2D(BNB_SAMPLER_2D(s_upscale_tex), var_uv.zw).xyz +
        textureOffset(BNB_SAMPLER_2D(s_upscale_tex), var_uv.zw, ivec2(-1, 0)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_upscale_tex), var_uv.zw, ivec2(0, -1)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_upscale_tex), var_uv.zw, ivec2(-1, -1)).xyz) +
        (1. / 12.) *
        (textureOffset(BNB_SAMPLER_2D(s_upscale_tex), var_uv.xy, ivec2(1, 0)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_upscale_tex), var_uv.xy, ivec2(-1, 0)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_upscale_tex), var_uv.xy, ivec2(0, 1)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_upscale_tex), var_uv.xy, ivec2(0, -1)).xyz);
    // clang-format on

    bnb_FragColor = vec4(sum, 1.);
}
