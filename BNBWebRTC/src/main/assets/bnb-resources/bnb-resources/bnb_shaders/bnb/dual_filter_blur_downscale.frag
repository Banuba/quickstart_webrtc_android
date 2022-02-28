#include <bnb/glsl.frag>

BNB_DECLARE_SAMPLER_2D(0, 1, s_downscale_tex);

BNB_IN(0)
vec2 var_uv;

void main()
{
    // clang-format off
    vec3 sum =
        0.5 *
        BNB_TEXTURE_2D(BNB_SAMPLER_2D(s_downscale_tex), var_uv).xyz +
        0.125 *
        (textureOffset(BNB_SAMPLER_2D(s_downscale_tex), var_uv, ivec2(1, 1)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_downscale_tex), var_uv, ivec2(-1, 1)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_downscale_tex), var_uv, ivec2(-1, -1)).xyz +
        textureOffset(BNB_SAMPLER_2D(s_downscale_tex), var_uv, ivec2(1, -1)).xyz);
    // clang-format on
    bnb_FragColor = vec4(sum, 1.);
}