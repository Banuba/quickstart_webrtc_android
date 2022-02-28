'use strict';

const modules_scene_index = require('../scene/index.js');

const mattVertexShader = "modules/lips/matt.vert";

const mattFragmentShader = "modules/lips/matt.frag";

class Lips {
    constructor() {
        Object.defineProperty(this, "_shared", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: {
                tex_camera: new modules_scene_index.Camera(),
                tex_lips_mask: new modules_scene_index.SegmentationMask("LIPS"),
                var_lips_color: new modules_scene_index.Vector4(0, 0, 0, 0),
                var_lips_saturation: new modules_scene_index.Vector4(1),
                var_lips_brightness: new modules_scene_index.Vector4(1),
            }
        });
        Object.defineProperty(this, "_matt", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: new modules_scene_index.Mesh(new modules_scene_index.QuadGeometry(), new modules_scene_index.ShaderMaterial({
                vertexShader: mattVertexShader,
                fragmentShader: mattFragmentShader,
                uniforms: {
                    tex_camera: this._shared.tex_camera,
                    tex_lips_mask: this._shared.tex_lips_mask,
                    var_lips_color: this._shared.var_lips_color,
                    var_lips_saturation: this._shared.var_lips_saturation,
                    var_lips_brightness: this._shared.var_lips_brightness,
                },
                state: {
                    backFaces: true
                }
            }))
        });
        const onChange = () => {
            const [, , , a] = this._shared.var_lips_color.value();
            const isColored = a > 0;
            if (!isColored) {
                this._matt.visible(false);
                return;
            }
            this._matt.material.uniforms.tex_lips_mask.enable();
            this._matt.visible(true);
        }
        this._shared.var_lips_color.subscribe(onChange);
        modules_scene_index.add(this._matt);
    }
    /**
     * Sets matt lips color
     * This is a helper method and equivalent of
     * ```js
     * Lips
     *  .color(rgba)
     *  .saturation(1)
     *  .shineIntensity(0)
     *  .shineBleeding(0)
     *  .shineScale(0)
     *  .glitterIntensity(0)
     *  .glitterBleeding(0)
     * ```
     */
    matt(color) {
        if (typeof color !== "undefined") {
            this.color(color);
            this.saturation(1);
            this.brightness(1);
        }
        return this.color();
    }

    /** Sets the lips color */
    color(color) {
        if (typeof color !== "undefined") {
            this._shared.var_lips_color.value(color);
        }
        return this._shared.var_lips_color.value().join(" ");
    }
    /** Sets the lips color saturation */
    saturation(value) {
        this._shared.var_lips_saturation.value(value);
    }
    /** Sets the lips color brightness */
    brightness(value) {
        this._shared.var_lips_brightness.value(value);
    }
    /** Sets the lips shine intensity */
    /** Removes the lips color, resets any setting applied */
    clear() {
        this.color("0 0 0 0");
        this.saturation(1);
        this.brightness(1);
    }
}

exports.Lips = Lips;
