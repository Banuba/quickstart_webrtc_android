#ifndef BNB_VERSION_GLSL
#define BNB_VERSION_GLSL

#ifdef GL_ES
    #if __VERSION__ == 100
        #define BNB_GL_ES_1 1
    #else
        #define BNB_GL_ES_3 1
    #endif
#else
    #ifdef VULKAN
        #define BNB_VK_1 1
    #else
        #define BNB_GL 1
    #endif
#endif

#endif // BNB_VERSION_GLSL
