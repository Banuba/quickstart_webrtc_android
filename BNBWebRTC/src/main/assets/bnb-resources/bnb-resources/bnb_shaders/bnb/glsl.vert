/**
 * Common vertex shader declaration. Always include first. Always 
 * include in `main` source only.
 * Other includes rely on it.
 */

#ifndef BNB_VERTEX_SHADER
#define BNB_VERTEX_SHADER

#include <bnb/version.glsl>
#include <bnb/samplers_declaration.glsl>
#include <bnb/textures_lookup.glsl>

#if defined(BNB_GL_ES_3) || defined(BNB_GL)
    #define BNB_IN in
    #define BNB_OUT(l) out
    #define BNB_LAYOUT_LOCATION(l) layout(location = l)
#elif defined(BNB_GL_ES_1)
    #define BNB_IN attribute
    #define BNB_OUT(l) varying
    #define BNB_LAYOUT_LOCATION(l)
    #define BNB_CENTROID
#elif defined(BNB_VK_1)
    #define BNB_IN in
    #define BNB_OUT(l) layout(location = l) out
    #define BNB_LAYOUT_LOCATION(l) layout(location = l)
out gl_PerVertex
{
    vec4 gl_Position;
    float gl_PointSize;
};
#endif

#ifdef BNB_VK_1
    #define gl_VertexID gl_VertexIndex
    #define gl_InstanceID gl_InstanceIndex
#endif


#ifdef BNB_GL_ES_1
uniform int fxr_InstanceID;
    #define gl_InstanceID fxr_InstanceID
#endif


#endif // BNB_VERTEX_SHADER