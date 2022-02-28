'use strict';

const modules_scene_index = require('../scene/index.js');

const colorFragmentShader = "modules/eyes/color.frag";

const colorVertexShader = "modules/eyes/color.vert";

class Eyes {
    constructor() {
        Object.defineProperty(this, "_color", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: new modules_scene_index.Mesh(new modules_scene_index.PlaneGeometry(), new modules_scene_index.ShaderMaterial({
                vertexShader: colorVertexShader,
                fragmentShader: colorFragmentShader,
                uniforms: {
                    tex_camera: new modules_scene_index.Camera(),
                    tex_l_eye_mask: new modules_scene_index.SegmentationMask("L_EYE"),
                    tex_r_eye_mask: new modules_scene_index.SegmentationMask("R_EYE"),
                    var_eyes_color: new modules_scene_index.Vector4(0, 0, 0, 0),
                },
            }))
        });
        const onChange = () => {
            const [, , , a] = this._color.material.uniforms.var_eyes_color.value();
            if (a > 0) {
                this._color.material.uniforms.tex_l_eye_mask.enable();
                this._color.material.uniforms.tex_r_eye_mask.enable();
            }
            else {
                this._color.material.uniforms.tex_l_eye_mask.disable();
                this._color.material.uniforms.tex_r_eye_mask.disable();
            }
            this._color.visible(a > 0);
        }
        this._color.material.uniforms.var_eyes_color.subscribe(onChange);
        modules_scene_index.add(this._color);
    }

    /** Sets the eyes color */
    color(color) {
        if (typeof color !== "undefined")
            this._color.material.uniforms.var_eyes_color.value(color);
        return this._color.material.uniforms.var_eyes_color.value().join(" ");
    }
    /** Removes the eyes color, resets any settings applied */
    clear() {
        this.color("0 0 0 0");
    }
}

exports.Eyes = Eyes;
