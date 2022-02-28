#ifndef BNB_LUT_GLSL
#define BNB_LUT_GLSL

/**
 * Following defines
 * `vec4 BNB_TEXTURE_LUT(vec4 original_color, BNB_DECLARE_SAMPLER_LUT_ARGUMENT(lookup_texture))`,
 * Where `lookup_texture` defined as
 * `BNB_DECLARE_SAMPLER_LUT(x, y) lookup_texture;`.
 * You can apply this LUT using following code:
 * `vec4 res = BNB_TEXTURE_LUT(original_color, BNB_PASS_SAMPLER_ARGUMENT(lookup_texture));`.
 * `vec3` overload is also present.
 */
#ifdef BNB_GL_ES_1
    #define BNB_DECLARE_SAMPLER_LUT BNB_DECLARE_SAMPLER_2D
    // #define BNB_SAMPLER_LUT BNB_SAMPLER_2D
    #define BNB_DECLARE_SAMPLER_LUT_ARGUMENT BNB_DECLARE_SAMPLER_2D_ARGUMENT
    #define BNB_TEXTURE_LUT bnb_texture_lookup_512
    #define BNB_TEXTURE_LUT_SMALL bnb_texture_lookup_16x256
#else
    #define BNB_DECLARE_SAMPLER_LUT BNB_DECLARE_SAMPLER_3D
    // #define BNB_SAMPLER_LUT BNB_SAMPLER_3D
    #define BNB_DECLARE_SAMPLER_LUT_ARGUMENT BNB_DECLARE_SAMPLER_3D_ARGUMENT
    #define BNB_TEXTURE_LUT bnb_texture_3d_lookup_512
    #define BNB_TEXTURE_LUT_SMALL bnb_texture_3d_lookup_16
    #define BNB_TEXTURE_LUT_LOD BNB_TEXTURE_3D_LOD
#endif // BNB_GL_ES_1

#define BNB_SAMPLER_LUT = BNB_PASS_SAMPLER_ARGUMENT

/**
 * Appply LUT to `original_color`. `lookup_texture` must be square 512x512 
 * 2D texture.
 * https://docs.unrealengine.com/en-US/RenderingAndGraphics/PostProcessEffects/UsingLUTs/index.html
 * Prefer `BNB_TEXTURE_LUT` instead of this call.
 */
vec4 bnb_texture_lookup_512(
    vec4 original_color,
    BNB_DECLARE_SAMPLER_2D_ARGUMENT(lookup_texture))
{
    const float epsilon = 0.000001;
    const float lut_size = 512.0;

    float blue_value = (original_color.b * 255.0) / 4.0;

    vec2 mul_b = clamp(floor(blue_value) + vec2(0.0, 1.0), 0.0, 63.0);
    vec2 row = floor(mul_b / 8.0 + epsilon);
    vec4 row_col = vec4(row, mul_b - row * 8.0);
    vec4 lookup = original_color.ggrr * (63.0 / lut_size)
                  + row_col * (64.0 / lut_size) + (0.5 / lut_size);

    float factor = blue_value - mul_b.x;

    vec3 sampled1 = BNB_TEXTURE_2D(
                        BNB_SAMPLER_2D(lookup_texture),
                        lookup.zx)
                        .rgb;
    vec3 sampled2 = BNB_TEXTURE_2D(
                        BNB_SAMPLER_2D(lookup_texture),
                        lookup.wy)
                        .rgb;

    vec3 res = mix(sampled1, sampled2, factor);
    return vec4(res, original_color.a);
}

vec3 bnb_texture_lookup_512(
    vec3 original_color,
    BNB_DECLARE_SAMPLER_2D_ARGUMENT(lookup_texture))
{
    return bnb_texture_lookup_512(
               vec4(original_color, 1.0),
               BNB_PASS_SAMPLER_ARGUMENT(lookup_texture))
        .rgb;
}

/**
 * Appply LUT to `original_color`. `lookup_texture` must be 16x256 
 * 2D texture.
 * https://docs.unrealengine.com/en-US/RenderingAndGraphics/PostProcessEffects/UsingLUTs/index.html
 * Prefer `BNB_TEXTURE_LUT` instead of this call.
 */
vec4 bnb_texture_lookup_16x256(
    vec4 original_color,
    BNB_DECLARE_SAMPLER_2D_ARGUMENT(lookup_texture))
{
    float blue_value = original_color.b * 15.;
    float blue_plane = floor(blue_value);
    float factor = blue_value - blue_plane;
    vec2 uv = original_color.rg * vec2(15. / 16., 15. / 256.) + vec2(0.5 / 16., 0.5 / 256.);
    uv.y += blue_plane / 16.;
    vec3 sampled1 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(lookup_texture), uv).rgb;
    uv.y += 1. / 16.;
    vec3 sampled2 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(lookup_texture), uv).rgb;

    vec3 res = mix(sampled1, sampled2, factor);
    return vec4(res, original_color.a);
}

vec3 bnb_texture_lookup_16x256(
    vec3 original_color,
    BNB_DECLARE_SAMPLER_2D_ARGUMENT(lookup_texture))
{
    return bnb_texture_lookup_16x256(vec4(original_color, 1.0), BNB_PASS_SAMPLER_ARGUMENT(lookup_texture)).rgb;
}

#ifndef BNB_GL_ES_1

/**
 * Prefer `BNB_TEXTURE_LUT` instead of this call.
 */
vec4 bnb_texture_3d_lookup_512(vec4 original_color, BNB_DECLARE_SAMPLER_3D_ARGUMENT(lookup_texture))
{
    return vec4(textureLod(BNB_SAMPLER_3D(lookup_texture), original_color.rgb * (63. / 64.) + 0.5 / 64., 0.).rgb, original_color.a);
}

vec3 bnb_texture_3d_lookup_512(vec3 original_color, BNB_DECLARE_SAMPLER_3D_ARGUMENT(lookup_texture))
{
    return textureLod(BNB_SAMPLER_3D(lookup_texture), original_color * (63. / 64.) + 0.5 / 64., 0.).rgb;
}

vec4 bnb_texture_3d_lookup_16(vec4 original_color, BNB_DECLARE_SAMPLER_3D_ARGUMENT(lookup_texture))
{
    return vec4(textureLod(BNB_SAMPLER_3D(lookup_texture), original_color.rgb * (15. / 16.) + 0.5 / 16., 0.).rgb, original_color.a);
}

vec3 bnb_texture_3d_lookup_16(vec3 original_color, BNB_DECLARE_SAMPLER_3D_ARGUMENT(lookup_texture))
{
    return textureLod(BNB_SAMPLER_3D(lookup_texture), original_color * (15. / 16.) + 0.5 / 16., 0.).rgb;
}

#endif // BNB_GL_ES_1

#endif // BNB_LUT_GLSL