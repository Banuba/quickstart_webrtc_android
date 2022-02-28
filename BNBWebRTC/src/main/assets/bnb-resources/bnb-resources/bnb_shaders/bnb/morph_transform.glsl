#ifndef BNB_MORPH_TRANSFORM_GLSL
#define BNB_MORPH_TRANSFORM_GLSL

#ifdef BNB_USE_AUTOMORPH
vec2 bnb_morph_coord(vec3 v)
{
    const float half_angle = radians(104.);
    const float y0 = -110.;
    const float y1 = 112.;
    float x = atan(v.x, v.z) / half_angle;
    float y = ((v.y - y0) / (y1 - y0)) * 2. - 1.;
    return vec2(x, y);
}

    #ifndef BNB_AUTOMORPH_BONE
vec3 bnb_auto_morph(vec3 v)
{
    vec2 morph_uv = bnb_morph_coord(v) * 0.5 + 0.5;
        #ifdef BNB_VK_1
    morph_uv.y = 1. - morph_uv.y;
        #endif
    vec3 translation = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_MORPH), morph_uv).xyz;
    return v + translation;
}
    #else
vec3 bnb_auto_morph_bone(vec3 v, mat4 m)
{
    vec2 morph_uv = bnb_morph_coord(vec3(m[0][3], m[1][3], m[2][3])) * 0.5 + 0.5;
        #ifdef BNB_VK_1
    morph_uv.y = 1. - morph_uv.y;
        #endif
    vec3 translation = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_MORPH), morph_uv).xyz;
    return v + translation;
}
    #endif // BNB_AUTOMORPH_BONE
#endif     // BNB_USE_AUTOMORPH
#endif     // BNB_MORPH_TRANSFORM_GLSL