#ifndef BNB_TEXTURE_BICUBIC_GLSL
#define BNB_TEXTURE_BICUBIC_GLSL

#include <bnb/textures_lookup.glsl>

vec4 cubic(float v)
{
    vec4 n = vec4(1.0, 2.0, 3.0, 4.0) - v;
    vec4 s = n * n * n;
    float x = s.x;
    float y = s.y - 4.0 * s.x;
    float z = s.z - 4.0 * s.y + 6.0 * s.x;
    float w = 6.0 - x - y - z;
    return vec4(x, y, z, w) * (1.0 / 6.0);
}

#if defined(BNB_GL_ES_1)
vec4 bnb_texture_bicubic(BNB_DECLARE_SAMPLER_2D_ARGUMENT(tex), vec2 uv)
{
    return BNB_TEXTURE_2D(BNB_PASS_SAMPLER_ARGUMENT(tex), uv);
}
#else
vec4 bnb_texture_bicubic(BNB_DECLARE_SAMPLER_2D_ARGUMENT(tex), vec2 uv)
{
    vec2 tex_size = vec2(textureSize(BNB_SAMPLER_2D(tex), 0));
    vec2 invtex_size = 1.0 / tex_size;

    uv = uv * tex_size - 0.5;

    vec2 fxy = fract(uv);
    uv -= fxy;

    vec4 xcubic = cubic(fxy.x);
    vec4 ycubic = cubic(fxy.y);

    vec4 c = uv.xxyy + vec2(-0.5, +1.5).xyxy;

    vec4 s = vec4(xcubic.xz + xcubic.yw, ycubic.xz + ycubic.yw);
    vec4 offset = c + vec4(xcubic.yw, ycubic.yw) / s;

    offset *= invtex_size.xxyy;

    vec4 sample0 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex), offset.xz);
    vec4 sample1 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex), offset.yz);
    vec4 sample2 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex), offset.xw);
    vec4 sample3 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(tex), offset.yw);

    float sx = s.x / (s.x + s.y);
    float sy = s.z / (s.z + s.w);

    return mix(
        mix(sample3, sample2, sx),
        mix(sample1, sample0, sx),
        sy);
}
#endif

#endif // BNB_TEXTURE_BICUBIC_GLSL
