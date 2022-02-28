#ifndef BNB_TEXTURES_LOOKUP_GLSL
#define BNB_TEXTURES_LOOKUP_GLSL


#if defined(BNB_GL_ES_3) || defined(BNB_GL)
    #define BNB_SAMPLER_2D(sampler_name) sampler_name
    #define BNB_SAMPLER_2D_ARRAY(sampler_name) sampler_name
    #define BNB_TEXTURE_2D(s, uv) texture(s, vec2(uv))
    #define BNB_TEXTURE_2D_ARRAY texture
    #define BNB_TEXTURE_2D_LOD textureLod
    #define BNB_TEXEL_FETCH_2D texelFetch


    #define BNB_SAMPLER_CUBE(sampler_name) sampler_name
    #define BNB_TEXTURE_CUBE texture
    #define BNB_TEXTURE_CUBE_LOD textureLod

    #define BNB_TEXTURE_3D texture
    #define BNB_TEXTURE_3D_LOD textureLod
    #define BNB_SAMPLER_3D(sampler_name) sampler_name

    #define BNB_CENTROID centroid
#elif defined(BNB_GL_ES_1)
    #define BNB_SAMPLER_2D(sampler_name) sampler_name
    #define BNB_TEXTURE_2D(s, uv) texture2D(s, vec2(uv))
    #define BNB_TEXTURE_CUBE textureCube

    #define BNB_SAMPLER_CUBE(sampler_name) sampler_name

    #if defined(BNB_VERTEX_SHADER)
        #define BNB_TEXTURE_2D_LOD texture2DLod
        #define BNB_TEXTURE_CUBE_LOD textureCubeLod
        #define BNB_TEXEL_FETCH_2D texture2DLod
    #elif defined(BNB_FRAGMENT_SHADER)
        #ifdef GL_EXT_shader_texture_lod
            #define BNB_TEXTURE_2D_LOD texture2DLodEXT
            #define BNB_TEXTURE_CUBE_LOD textureCubeLodEXT
            #define BNB_TEXEL_FETCH_2D texture2DLodEXT
        #else
            #define BNB_TEXTURE_2D_LOD texture2D
            #define BNB_TEXTURE_CUBE_LOD textureCube
            #define BNB_TEXEL_FETCH_2D texture2D
        #endif
    #endif

    //HACK: Fake functions just to avoid effects crashes in glsl 1.0
    #define texelFetch(s, texcords, lod) BNB_TEXTURE_2D(s, vec2(texcords))
    #define textureSize(s, lod) ivec2(0, 0)
    #define textureOffset(s, uv, offset) BNB_TEXTURE_2D(s, vec2(uv))

    #define BNB_CENTROID
#elif defined(BNB_VK_1)
    #define BNB_SAMPLER_2D(sampler_name) sampler2D(texture_##sampler_name, sampler_##sampler_name)
    #define BNB_SAMPLER_2D_ARRAY(sampler_name) sampler2DArray(texture_##sampler_name, sampler_##sampler_name)
    #define BNB_TEXTURE_2D(s, uv) texture(s, vec2(uv))
    #define BNB_TEXTURE_2D_ARRAY texture
    #define BNB_TEXTURE_2D_LOD textureLod
    #define BNB_TEXEL_FETCH_2D texelFetch

    #define BNB_SAMPLER_CUBE(sampler_name) samplerCube(texture_##sampler_name, sampler_##sampler_name)
    #define BNB_TEXTURE_CUBE texture
    #define BNB_TEXTURE_CUBE_LOD textureLod

    #define BNB_TEXTURE_3D texture
    #define BNB_TEXTURE_3D_LOD textureLod
    #define BNB_SAMPLER_3D(sampler_name) sampler3D(texture_##sampler_name, sampler_##sampler_name)

    #define BNB_CENTROID centroid
#endif

#endif // BNB_TEXTURES_LOOKUP_GLSL
