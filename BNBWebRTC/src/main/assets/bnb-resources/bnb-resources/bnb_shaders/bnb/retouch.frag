#include <bnb/glsl.frag>
#include <bnb/lut.glsl>

BNB_IN(0)
vec3 maskColor;
BNB_IN(1)
vec4 var_uv_bg_uv;

BNB_DECLARE_SAMPLER_LUT(0, 1, lookupTexTeeth);
BNB_DECLARE_SAMPLER_LUT(2, 3, lookupTexEyes);

BNB_DECLARE_SAMPLER_2D(4, 5, tex_softLight);
BNB_DECLARE_SAMPLER_2D(6, 7, tex_normalMakeup);
BNB_DECLARE_SAMPLER_2D(8, 9, bnb_BACKGROUND);


vec3 whitening(vec3 originalColor, float factor, BNB_DECLARE_SAMPLER_LUT_ARGUMENT(lookup))
{
    vec3 color = BNB_TEXTURE_LUT(originalColor, BNB_PASS_SAMPLER_ARGUMENT(lookup));
    return mix(originalColor, originalColor, factor);
}


vec3 sharpen(vec3 originalColor, float factor)
{
    const float dx = 1.0 / 960.0;
    const float dy = 1.0 / 1280.0;

    vec3 total = 5.0 * originalColor
                 - BNB_TEXTURE_2D_LOD(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z - dx, var_uv_bg_uv.w - dy), 0.).xyz
                 - BNB_TEXTURE_2D_LOD(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z + dx, var_uv_bg_uv.w - dy), 0.).xyz
                 - BNB_TEXTURE_2D_LOD(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z - dx, var_uv_bg_uv.w + dy), 0.).xyz
                 - BNB_TEXTURE_2D_LOD(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z + dx, var_uv_bg_uv.w + dy), 0.).xyz;

    vec3 result = mix(originalColor, total, factor);
    return clamp(result, 0.0, 1.0);
}


vec3 softSkin(vec3 originalColor, float factor)
{
    vec3 screenColor = originalColor;

    const float dx = 4.5 / 960.0;
    const float dy = 4.5 / 1280.0;

    vec3 nextColor0 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z - dx, var_uv_bg_uv.w - dy)).xyz;
    vec3 nextColor1 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z + dx, var_uv_bg_uv.w - dy)).xyz;
    vec3 nextColor2 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z - dx, var_uv_bg_uv.w + dy)).xyz;
    vec3 nextColor3 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BACKGROUND), vec2(var_uv_bg_uv.z + dx, var_uv_bg_uv.w + dy)).xyz;

    float intensity = screenColor.g;
    vec4 nextIntensity = vec4(nextColor0.g, nextColor1.g, nextColor2.g, nextColor3.g);
    vec4 lg = nextIntensity - intensity;

    vec4 curr = max(0.367 - abs(lg * (0.367 * 0.6 / (1.41 * PSI.x))), 0.);

    float summ = 1.0 + curr.x + curr.y + curr.z + curr.w;
    screenColor += (nextColor0 * curr.x + nextColor1 * curr.y + nextColor2 * curr.z + nextColor3 * curr.w);
    screenColor = screenColor * (factor / summ);

    screenColor = originalColor * (1. - factor) + screenColor;
    return screenColor;
}


float softlight_blend_1ch(float a, float b)
{
    return ((1. - 2. * b) * a + 2. * b) * a;
}


vec3 blendSoftLight(vec3 base, vec3 blend)
{
    return vec3(softlight_blend_1ch(base.r, blend.r), softlight_blend_1ch(base.g, blend.g), softlight_blend_1ch(base.b, blend.b));
}


void main()
{
    vec3 res = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BACKGROUND), var_uv_bg_uv.zw).xyz;
    res = softSkin(res, maskColor.r * skinSoftIntensity.x);

    if (maskColor.g > 1. / 255.) {
        float sharp_factor = maskColor.g * teethSharpenIntensity.x;
        res = sharpen(res, sharp_factor);
        float teeth_factor = maskColor.g;
        res = whitening(res, teeth_factor, BNB_PASS_SAMPLER_ARGUMENT(lookupTexTeeth));
    }

    res = sharpen(res, maskColor.b * eyesSharpenIntensity.x);
    res = whitening(res, maskColor.b * eyesWhiteningCoeff.x, BNB_PASS_SAMPLER_ARGUMENT(lookupTexEyes));
    vec2 uvh = vec2(abs(2.0 * (var_uv_bg_uv.x - 0.5)), var_uv_bg_uv.y);
    res.xyz = blendSoftLight(res.xyz, BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_softLight), uvh).xyz);
    vec4 makeup2 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex_normalMakeup), var_uv_bg_uv.xy);
    res.xyz = mix(res.xyz, makeup2.xyz, makeup2.w);

    bnb_FragColor = vec4(res, 1.);
}