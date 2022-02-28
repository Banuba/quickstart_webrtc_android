/**
 * Common frgament sgader declaration. Always make it as first include.
 * Other includes rely on it.
 */


#ifndef BNB_FRAGMENT_SHADER
#define BNB_FRAGMENT_SHADER

#include <bnb/version.glsl>

#include <bnb/samplers_declaration.glsl>
#include <bnb/textures_lookup.glsl>

//---------- In-out ----------

#if defined(BNB_GL_ES_1)
    #define BNB_IN(l) varying
    #define bnb_FragColor gl_FragColor
    #define BNB_CENTROID
#elif defined(BNB_GL_ES_3) || defined(BNB_GL)
    #define BNB_IN(l) in
// declare out color
layout(location = 0) out vec4 bnb_FragColor;
    #define BNB_CENTROID centroid
#else
    #define BNB_IN(l) layout(location = l) in
layout(location = 0) out vec4 bnb_FragColor;
    #define BNB_CENTROID centroid
#endif

#endif // BNB_FRAGMENT_SHADER