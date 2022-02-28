
exports = {
    BNBApplyLightStreaks: (function() {
        const assetManager = bnb.scene.getAssetManager();

        const initAttachment = assetManager.createImage("light_streak_fullscreen_init_attachment__", bnb.ImageType.ATTACHMENT);
        const initRT = assetManager.createRenderTarget("light_streak_fullscreen_init_RT__");
        initRT.addAttachment(initAttachment);
        initRT.setScale(1 / 4);

        const fullscreenPolygon = assetManager.createMesh("light_streak_fullscreen_poly__");
        assetManager.uploadMeshData(fullscreenPolygon, "$builtin$meshes/fs_tri");

        const thresholdParam = bnb.Parameter.create("light_streak_threshold");
        thresholdParam.setVector4(new bnb.Vec4(0, 0, 0, 0));

        const initMaterial = assetManager.createMaterial("init_light_streak", "$resources$/bnb_shaders/bnb/init_light_streaks");
        initMaterial.addImage("s", null);
        initMaterial.addParameter(thresholdParam);
        initMaterial.setState(new bnb.State(bnb.BlendingMode.OFF, false, false, true, true));

        const initLayer = bnb.Layer.create("light_streak_fullscreen_init_layer__");
        const initEntity = bnb.scene.createEntity("light_streak_fullscreen_init_entity__");

        const initMeshInstance = bnb.MeshInstance.create();
        initMeshInstance.setVisible(true);
        initMeshInstance.setMesh(fullscreenPolygon);
        initMeshInstance.setSubGeometryMaterial("built_in_triangle", initMaterial);

        initEntity.addIntoLayer(initLayer);
        initEntity.addComponent(initMeshInstance.asComponent());

        bnb.scene.getRoot().addChild(initEntity);
        bnb.scene.getRenderList().addTask(initLayer, initRT);



        const filterAttachment0 = assetManager.createImage("light_streak_fullscreen_filter_attachment0__", bnb.ImageType.ATTACHMENT);
        const filterRT0 = assetManager.createRenderTarget("light_streak_fullscreen_filter_RT0__");
        filterRT0.addAttachment(filterAttachment0);
        filterRT0.setScale(1 / 4);

        const filterAttachment1 = assetManager.createImage("light_streak_fullscreen_filter_attachment1__", bnb.ImageType.ATTACHMENT);
        const filterRT1 = assetManager.createRenderTarget("light_streak_fullscreen_filter_RT1__");
        filterRT1.addAttachment(filterAttachment1);
        filterRT1.setScale(1 / 4);

        const filterAttachment2 = assetManager.createImage("light_streak_fullscreen_filter_attachment2__", bnb.ImageType.ATTACHMENT);
        const filterRT2 = assetManager.createRenderTarget("light_streak_fullscreen_filter_RT2__");
        filterRT2.addAttachment(filterAttachment2);
        filterRT2.setScale(1 / 4);

        const filterAttachment3 = assetManager.createImage("light_streak_fullscreen_filter_attachment3__", bnb.ImageType.ATTACHMENT);
        const filterRT3 = assetManager.createRenderTarget("light_streak_fullscreen_filter_RT3__");
        filterRT3.addAttachment(filterAttachment3);
        filterRT3.setScale(1 / 4);

        const filterMaterial0 = assetManager.createMaterial("filter_light_streaks_0", "$resources$/bnb_shaders/bnb/filter_light_streaks_0");
        filterMaterial0.addImage("s", initAttachment);
        filterMaterial0.setState(new bnb.State(bnb.BlendingMode.OFF, false, false, true, true));

        const filterMaterial1 = assetManager.createMaterial("filter_light_streaks_1", "$resources$/bnb_shaders/bnb/filter_light_streaks_1");
        filterMaterial1.addImage("s", filterAttachment0);
        filterMaterial1.setState(new bnb.State(bnb.BlendingMode.OFF, false, false, true, true));

        const filterMaterial2 = assetManager.createMaterial("filter_light_streaks_2", "$resources$/bnb_shaders/bnb/filter_light_streaks_2");
        filterMaterial2.addImage("s", filterAttachment1);
        filterMaterial2.setState(new bnb.State(bnb.BlendingMode.OFF, false, false, true, true));

        const filterMaterial3 = assetManager.createMaterial("filter_light_streaks_3", "$resources$/bnb_shaders/bnb/filter_light_streaks_3");
        filterMaterial3.addImage("s", filterAttachment2);
        filterMaterial3.setState(new bnb.State(bnb.BlendingMode.OFF, false, false, true, true));


        const filterMeshInstance0 = bnb.MeshInstance.create();
        filterMeshInstance0.setVisible(true);
        filterMeshInstance0.setMesh(fullscreenPolygon);
        filterMeshInstance0.setSubGeometryMaterial("built_in_triangle", filterMaterial0);

        const filterLayer0 = bnb.Layer.create("light_streak_fullscreen_filter_layer0__");
        const filterEntity0 = bnb.scene.createEntity("light_streak_fullscreen_filter_layer0__");
        filterEntity0.addComponent(filterMeshInstance0.asComponent());
        filterEntity0.addIntoLayer(filterLayer0);

        bnb.scene.getRoot().addChild(filterEntity0);
        bnb.scene.getRenderList().addTask(filterLayer0, filterRT0);

        const filterMeshInstance1 = bnb.MeshInstance.create();
        filterMeshInstance1.setVisible(true);
        filterMeshInstance1.setMesh(fullscreenPolygon);
        filterMeshInstance1.setSubGeometryMaterial("built_in_triangle", filterMaterial1);

        const filterLayer1 = bnb.Layer.create("light_streak_fullscreen_filter_layer1__");
        const filterEntity1 = bnb.scene.createEntity("light_streak_fullscreen_filter_layer1__");
        filterEntity1.addComponent(filterMeshInstance1.asComponent());
        filterEntity1.addIntoLayer(filterLayer1);

        bnb.scene.getRoot().addChild(filterEntity1);
        bnb.scene.getRenderList().addTask(filterLayer1, filterRT1);

        const filterMeshInstance2 = bnb.MeshInstance.create();
        filterMeshInstance2.setVisible(true);
        filterMeshInstance2.setMesh(fullscreenPolygon);
        filterMeshInstance2.setSubGeometryMaterial("built_in_triangle", filterMaterial2);

        const filterLayer2 = bnb.Layer.create("light_streak_fullscreen_filter_layer2__");
        const filterEntity2 = bnb.scene.createEntity("light_streak_fullscreen_filter_layer2__");
        filterEntity2.addComponent(filterMeshInstance2.asComponent());
        filterEntity2.addIntoLayer(filterLayer2);

        bnb.scene.getRoot().addChild(filterEntity2);
        bnb.scene.getRenderList().addTask(filterLayer2, filterRT2);

        const filterMeshInstance3 = bnb.MeshInstance.create();
        filterMeshInstance3.setVisible(true);
        filterMeshInstance3.setMesh(fullscreenPolygon);
        filterMeshInstance3.setSubGeometryMaterial("built_in_triangle", filterMaterial3);

        const filterLayer3 = bnb.Layer.create("light_streak_fullscreen_filter_layer3__");
        const filterEntity3 = bnb.scene.createEntity("light_streak_fullscreen_filter_layer3__");
        filterEntity3.addComponent(filterMeshInstance3.asComponent());
        filterEntity3.addIntoLayer(filterLayer3);

        bnb.scene.getRoot().addChild(filterEntity3);
        bnb.scene.getRenderList().addTask(filterLayer3, filterRT3);



        const applyAttachment = assetManager.createImage("light_streak_fullscreen_apply_attachment__", bnb.ImageType.ATTACHMENT);
        const applyRT = assetManager.createRenderTarget("light_streak_fullscreen_apply_RT__");
        applyRT.addAttachment(applyAttachment);

        const colorParam = bnb.Parameter.create("light_streaks_color");
        colorParam.setVector4(new bnb.Vec4(0, 0, 0, 0));

        const copyPixelsMaterial = assetManager.createMaterial("$builtin$materials/copy_pixels.100000000000", "");
        copyPixelsMaterial.addImage("tex_src", null);
        copyPixelsMaterial.setState(new bnb.State(bnb.BlendingMode.OFF, false, false, true, true));

        const applyMaterial = assetManager.createMaterial("apply_light_streak", "$resources$/bnb_shaders/bnb/apply_light_streaks");
        applyMaterial.addImage("s", filterAttachment3);
        applyMaterial.addParameter(colorParam);
        applyMaterial.setState(new bnb.State(bnb.BlendingMode.OFF, false, false, true, true));


        const copyMeshInstance = bnb.MeshInstance.create();
        copyMeshInstance.setVisible(true);
        copyMeshInstance.setMesh(fullscreenPolygon);
        copyMeshInstance.setSubGeometryMaterial("built_in_triangle", copyPixelsMaterial);

        const copyLayer = bnb.Layer.create("light_streak_fullscreen_copy_layer__");
        const copyEntity = bnb.scene.createEntity("light_streak_fullscreen_copy_entity__");
        copyEntity.addComponent(copyMeshInstance.asComponent());
        copyEntity.addIntoLayer(copyLayer);

        bnb.scene.getRoot().addChild(copyEntity);
        bnb.scene.getRenderList().addTask(copyLayer, applyRT);

        const applyMeshInstance = bnb.MeshInstance.create();
        applyMeshInstance.setVisible(true);
        applyMeshInstance.setMesh(fullscreenPolygon);
        applyMeshInstance.setSubGeometryMaterial("built_in_triangle", applyMaterial);

        const applyLayer = bnb.Layer.create("light_streak_fullscreen_apply_layer__");
        const applyEntity = bnb.scene.createEntity("light_streak_fullscreen_apply_entity__");
        applyEntity.addComponent(applyMeshInstance.asComponent());
        applyEntity.addIntoLayer(applyLayer);

        bnb.scene.getRoot().addChild(applyEntity);
        bnb.scene.getRenderList().addTask(applyLayer, applyRT);

        return function(inputImage, applyImage, threshold, color, blend) {
            thresholdParam.setVector4(new bnb.Vec4(threshold, 0, 0, 1));
            initMaterial.addImage("s", inputImage);
            copyPixelsMaterial.addImage("tex_src", applyImage);
            applyMaterial.setState(new bnb.State(blend, false, false, true, true));
            colorParam.setVector4(new bnb.Vec4(color[0], color[1], color[2], color[3]));
        }
    })()
};