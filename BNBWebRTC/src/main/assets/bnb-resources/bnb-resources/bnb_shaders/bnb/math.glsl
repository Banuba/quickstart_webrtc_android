#ifndef BNB_MATH_GLSL
#define BNB_MATH_GLSL


#if defined(BNB_GL_ES_1)

float round(float x)
{
    return float(int(x + 0.5));
}

vec2 round(vec2 v)
{
    return vec2(round(v.x), round(v.y));
}

vec3 round(vec3 v)
{
    return vec3(round(v.xy), round(v.z));
}

vec4 round(vec4 v)
{
    return vec4(round(v.xyz), round(v.w));
}

#endif // BNB_GL_ES_1
#endif // BNB_MATH_GLSL