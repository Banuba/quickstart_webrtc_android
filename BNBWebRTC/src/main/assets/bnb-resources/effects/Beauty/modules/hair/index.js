'use strict';

const modules_scene_index = require('../scene/index.js');

const fragmentShader = "modules/hair/hair.frag";

const vertexShader = "modules/hair/hair.vert";

var ColoringMode;
(function (ColoringMode) {
    /* 0 */ ColoringMode[ColoringMode["SolidColor"] = 0] = "SolidColor";
    /* 1 */ ColoringMode[ColoringMode["Gradient"] = 1] = "Gradient";
    /* 2 */ ColoringMode[ColoringMode["Strands"] = 2] = "Strands";
})(ColoringMode || (ColoringMode = {}));
class Hair {
    constructor() {
        Object.defineProperty(this, "_hair", {
            enumerable: true,
            configurable: true,
            writable: true,
            value: new modules_scene_index.Mesh(new modules_scene_index.PlaneGeometry(), new modules_scene_index.ShaderMaterial({
                vertexShader,
                fragmentShader,
                uniforms: {
                    tex_camera: new modules_scene_index.Camera(),
                    tex_hair_mask: new modules_scene_index.SegmentationMask("HAIR"),
                    var_hair_color0: new modules_scene_index.Vector4(0, 0, 0, 0),
                    var_hair_color1: new modules_scene_index.Vector4(0, 0, 0, 0),
                    var_hair_color2: new modules_scene_index.Vector4(0, 0, 0, 0),
                    var_hair_color3: new modules_scene_index.Vector4(0, 0, 0, 0),
                    var_hair_color4: new modules_scene_index.Vector4(0, 0, 0, 0),
                    var_hair_colors_count: new modules_scene_index.Vector4(0),
                    var_hair_coloring_mode: new modules_scene_index.Vector4(ColoringMode.SolidColor),
                },
            }))
        });
        const onChange = () => {
            let isColored = [
                this._hair.material.uniforms.var_hair_color0.value(),
                this._hair.material.uniforms.var_hair_color1.value(),
                this._hair.material.uniforms.var_hair_color2.value(),
                this._hair.material.uniforms.var_hair_color3.value(),
                this._hair.material.uniforms.var_hair_color4.value(),
            ].some(([, , , a]) => a > 0);
            isColored && this._hair.material.uniforms.tex_hair_mask.enable();
            this._hair.visible(isColored);
        };
        this._hair.material.uniforms.var_hair_color0.subscribe(onChange);
        this._hair.material.uniforms.var_hair_color1.subscribe(onChange);
        this._hair.material.uniforms.var_hair_color2.subscribe(onChange);
        this._hair.material.uniforms.var_hair_color3.subscribe(onChange);
        this._hair.material.uniforms.var_hair_color4.subscribe(onChange);
        modules_scene_index.add(this._hair);
    }
    color(first, ...rest) {
        var _a;
        if (Array.isArray(first))
            return this.color(...first);
        const colors = [first, ...rest];
        this._hair.material.uniforms.var_hair_colors_count.value(colors.length);
        this._hair.material.uniforms.var_hair_coloring_mode.value(0);
        for (let i = 0; i < 5; ++i) {
            const color = (_a = colors[i]) !== null && _a !== void 0 ? _a : "0 0 0 0";
            const idx = i;
            const uniform = `var_hair_color${idx}`;
            this._hair.material.uniforms[uniform].value(color);
        }
    }
    clear() {
        this.color("0 0 0 0");
    }
}

exports.Hair = Hair;
