#include <bnb/glsl.frag>

BNB_IN(0)
vec2 var_uv;
BNB_IN(1)
vec2 var_face_uv;

BNB_DECLARE_SAMPLER_2D(0, 1, tex_lashes);
BNB_DECLARE_SAMPLER_2D(2, 3, tex_makeup);

vec4 blend(vec4 base, vec4 target, BNB_DECLARE_SAMPLER_2D_ARGUMENT(tex_mask))
{
    vec4 tex = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_mask), var_face_uv);
    tex.rgb += target.rgb;
    tex.a *= target.a;

    // https://gist.github.com/JordanDelcros/518396da1c13f75ee057
    if (tex.a == 0.)
        return base;
    if (base.a == 0.)
        return tex;

    float a = 1. - (1. - base.a) * (1. - tex.a);
    vec3 rgb = mix(base.rgb * base.a, tex.rgb, tex.a) / a;

    return vec4(rgb, a);
}

vec4 blend(vec4 base, BNB_DECLARE_SAMPLER_2D_ARGUMENT(tex_mask))
{
    return blend(base, vec4(0., 0., 0., 1.), BNB_PASS_SAMPLER_ARGUMENT(tex_mask));
}

void main()
{
    bnb_FragColor = vec4(0.);

    bnb_FragColor = blend(bnb_FragColor, BNB_PASS_SAMPLER_ARGUMENT(tex_makeup));
    bnb_FragColor = blend(bnb_FragColor, var_lashes_color, BNB_PASS_SAMPLER_ARGUMENT(tex_lashes));
}