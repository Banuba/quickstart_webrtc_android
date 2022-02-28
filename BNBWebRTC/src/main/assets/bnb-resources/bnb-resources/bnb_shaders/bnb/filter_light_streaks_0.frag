#include <bnb/glsl.frag>

BNB_IN(0)
vec4 var_uv_off;

BNB_DECLARE_SAMPLER_2D(0, 1, s);

void main()
{
    vec2 uv = var_uv_off.xy;
    vec2 d = var_uv_off.zw;

    float s0 = 2. * 0.20236;
    float s1 = 0.124009 + 0.179044;
    float s2 = 0.028532 + 0.067234;

    float o1 = 1. + 0.179044 / s1;
    float o2 = 3. + 0.067234 / s2;

    vec4 c = s0 * BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv);

    vec2 uv_off = d * o1;
    c += s1 * vec4(BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv + uv_off).x, BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv + vec2(uv_off.x, -uv_off.y)).y, BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv - uv_off).z, BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv + vec2(-uv_off.x, uv_off.y)).w);

    uv_off = d * o2;
    c += s2 * vec4(BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv + uv_off).x, BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv + vec2(uv_off.x, -uv_off.y)).y, BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv - uv_off).z, BNB_TEXTURE_2D(BNB_SAMPLER_2D(s), uv + vec2(-uv_off.x, uv_off.y)).w);

    bnb_FragColor = c * 1.3;
}