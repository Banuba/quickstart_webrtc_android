#ifndef BNB_SAMPLERS_DECLARATION_GLSL
#define BNB_SAMPLERS_DECLARATION_GLSL


// clang-format off

#ifdef BNB_VK_1
    #define BNB_DECLARE_SAMPLER_2D(binding_index_1, binding_index_2, sampler_name) layout(set = 1, binding = binding_index_1) uniform texture2D texture_##sampler_name;layout(set = 1, binding = binding_index_2) uniform sampler sampler_##sampler_name

    #define BNB_DECLARE_SAMPLER_2D_ARRAY(binding_index_1, binding_index_2, sampler_name) layout(set = 1, binding = binding_index_1) uniform texture2DArray texture_##sampler_name; layout(set = 1, binding = binding_index_2) uniform sampler sampler_##sampler_name

    #define BNB_DECLARE_SAMPLER_CUBE(binding_index_1, binding_index_2, sampler_name) layout(set = 1, binding = binding_index_1) uniform textureCube texture_##sampler_name;layout(set = 1, binding = binding_index_2) uniform sampler sampler_##sampler_name

    #define BNB_DECLARE_SAMPLER_3D(binding_index_1, binding_index_2, sampler_name) layout(set = 1, binding = binding_index_1) uniform texture3D texture_##sampler_name; layout(set = 1, binding = binding_index_2) uniform sampler sampler_##sampler_name

    #define BNB_DECLARE_SAMPLER_2D_ARGUMENT(arg_name) texture2D texture_##arg_name, sampler sampler_##arg_name
    #define BNB_DECLARE_SAMPLER_2D_ARRAY_ARGUMENT(arg_name) texture2DArray texture_##arg_name, sampler sampler_##arg_name
    #define BNB_DECLARE_SAMPLER_CUBE_ARGUMENT(arg_name) textureCube texture_##arg_name, sampler sampler_##arg_name
    #define BNB_DECLARE_SAMPLER_3D_ARGUMENT(arg_name) texture3D texture_##arg_name, sampler sampler_##arg_name

    #define BNB_PASS_SAMPLER_ARGUMENT(arg_name) texture_##arg_name, sampler_##arg_name

#else
    #define BNB_DECLARE_SAMPLER_2D(binding_index_1, binding_index_2, sampler_name) uniform sampler2D sampler_name
    #define BNB_DECLARE_SAMPLER_2D_ARRAY(binding_index_1, binding_index_2, sampler_name) uniform sampler2DArray sampler_name
    #define BNB_DECLARE_SAMPLER_CUBE(binding_index_1, binding_index_2, sampler_name) uniform samplerCube sampler_name
    #define BNB_DECLARE_SAMPLER_3D(binding_index_1, binding_index_2, sampler_name) uniform sampler3D sampler_name

    #define BNB_DECLARE_SAMPLER_2D_ARGUMENT(arg_name) sampler2D arg_name
    #define BNB_DECLARE_SAMPLER_2D_ARRAY_ARGUMENT(arg_name) sampler2DArray arg_name
    #define BNB_DECLARE_SAMPLER_CUBE_ARGUMENT(arg_name) samplerCube arg_name
    #define BNB_DECLARE_SAMPLER_3D_ARGUMENT(arg_name) sampler3D arg_name

    #define BNB_PASS_SAMPLER_ARGUMENT(arg_name) arg_name
#endif

#define BNB_DECLARE_SAMPLER_VIDEO BNB_DECLARE_SAMPLER_2D

// clang-format on

#endif // BNB_SAMPLERS_DECLARATION_GLSL