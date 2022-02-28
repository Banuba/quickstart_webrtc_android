#ifndef BNB_DECODE_INT1010102_GLSL
#define BNB_DECODE_INT1010102_GLSL

#ifdef BNB_GL_ES_1
vec4 bnb_decode_int1010102(vec4 bytes)
{
    float ux = bytes[0] + mod(bytes[1], 4.) * 256.;
    float uy = floor(bytes[1] / 4.) + mod(bytes[2], 16.) * 64.;
    float uz = floor(bytes[2] / 16.) + mod(bytes[3], 64.) * 16.;
    float uw = floor(bytes[3] / 64.);
    float x = (ux <= 511. ? ux : ux - 1024.) / 511.;
    float y = (uy <= 511. ? uy : uy - 1024.) / 511.;
    float z = (uz <= 511. ? uz : uz - 1024.) / 511.;
    float w = uw <= 1. ? uw : uw - 4.;
    return vec4(x, y, z, w);
}
#elif defined(BNB_VK_1)
vec4 bnb_decode_int1010102(uint u)
{
    float ux = u & 1023u;
    float uy = (u >> 10u) & 1023u;
    float uz = (u >> 20u) & 1023u;
    float uw = u >> 30u;
    float x = float(ux <= 511u ? ux : ux - 1024u) / 511.;
    float y = float(uy <= 511u ? uy : uy - 1024u) / 511.;
    float z = float(uz <= 511u ? uz : uz - 1024u) / 511.;
    float w = float(uw <= 1u ? uw : uw - 4u);
    return vec4(x, y, z, w);
}
#else
    #define bnb_decode_int1010102(v) v
#endif

/** decode_int1010102 is DEPRECATED */
#define decode_int1010102 bnb_decode_int1010102

#endif // BNB_DECODE_INT1010102_GLSL