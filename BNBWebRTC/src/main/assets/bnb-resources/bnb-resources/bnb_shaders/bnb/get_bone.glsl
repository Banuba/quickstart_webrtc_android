#ifndef BNB_GET_BONE_GLSL
#define BNB_GET_BONE_GLSL

#ifdef BNB_GL_ES_1
mat4 bnb_get_bone(float b, float db, float y)
{
    vec4 v0 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BONES), vec2(b, y));
    b += db;
    vec4 v1 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BONES), vec2(b, y));
    b += db;
    vec4 v2 = BNB_TEXTURE_2D(BNB_SAMPLER_2D(bnb_BONES), vec2(b, y));

    return mat4(v0, v1, v2, vec4(0., 0., 0., 1.));
}
#else
mat4 bnb_get_bone(uint bone_idx, int y)
{
    int b = int(bone_idx) * 3;
    mat4 m = mat4(
        texelFetch(BNB_SAMPLER_2D(bnb_BONES), ivec2(b, y), 0),
        texelFetch(BNB_SAMPLER_2D(bnb_BONES), ivec2(b + 1, y), 0),
        texelFetch(BNB_SAMPLER_2D(bnb_BONES), ivec2(b + 2, y), 0),
        vec4(0., 0., 0., 1.));
    return m;
}
#endif // BNB_GL_ES_1

#endif // BNB_GET_BONE_GLSL