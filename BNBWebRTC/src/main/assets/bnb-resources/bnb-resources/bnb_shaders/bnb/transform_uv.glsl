#ifndef BNB_TRANSFORM_UV
#define BNB_TRANSFORM_UV
// https://gist.github.com/ayamflow/c06bc0c8a64f985dd431bd0ac5b557cd
vec2 bnb_rotate_uv(vec2 uv, float rotation)
{
    const float mid = 0.5;

    return vec2(
        cos(rotation) * (uv.x - mid) + sin(rotation) * (uv.y - mid) + mid,
        cos(rotation) * (uv.y - mid) - sin(rotation) * (uv.x - mid) + mid);
}

vec2 bnb_scale_uv(vec2 uv, vec2 scale)
{
    const float mid = 0.5;

    scale = 1. / scale;

    return (uv - mid) * scale + mid;
}

const float aspect_scale_to_fill = 0.;
const float aspect_fill = 1.;
const float aspect_fit = 2.;


bool bnb_float_eq(float a, float b, float prec)
{
    if (sign(a) != sign(b)) {
        return false;
    }

    return abs(sign(a) * a - sign(b) * b) < prec;
}


bool bnb_float_eq(float a, float b)
{
    return bnb_float_eq(a, b, 0.0001);
}


vec2 bnb_contain_uv(vec2 uv, vec2 tex_size, float content_mode, float angle)
{
    const float mid = 0.5;
    float tex_aspect = tex_size.x / tex_size.y;
    if (bnb_float_eq(angle, 90., 0.1) || bnb_float_eq(angle, -90., 0.1) || bnb_float_eq(angle, 270., 0.1) || bnb_float_eq(angle, -270., 0.1)) {
        tex_aspect = tex_size.y / tex_size.x;
    }

    float screen_aspect = bnb_SCREEN.x / bnb_SCREEN.y;

    float ratio = tex_aspect / screen_aspect;

    float x = 1.;
    float y = 1.;

    if (bnb_float_eq(content_mode, aspect_fill, 0.1)) {
        if (bnb_float_eq(angle, 90., 0.1) || bnb_float_eq(angle, -90., 0.1) || bnb_float_eq(angle, 270., 0.1) || bnb_float_eq(angle, -270., 0.1)) {
            if (ratio > 1.) {
                y /= ratio;
            } else {
                x *= ratio;
            }
        } else {
            if (ratio > 1.)
                x /= ratio;
            else
                y *= ratio;
        }
        // } else if (content_mode == aspect_scale_to_fill) {
        // this is the default GL behaviuor, skip the `if` entirely
    } else if (bnb_float_eq(content_mode, aspect_fit, 0.1)) {
        if (bnb_float_eq(angle, 90., 0.1) || bnb_float_eq(angle, -90., 0.1) || bnb_float_eq(angle, 270., 0.1) || bnb_float_eq(angle, -270., 0.1)) {
            if (ratio > 1.) {
                x *= ratio;
            } else {
                y /= ratio;
            }
        } else {
            if (ratio > 1.)
                y *= ratio;
            else
                x /= ratio;
        }
    }

    return vec2(x, y) * (uv - mid) + mid;
}
#endif