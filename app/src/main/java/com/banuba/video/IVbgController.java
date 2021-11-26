package com.banuba.video;

public abstract class IVbgController {
    public abstract void initComponents();


    public abstract void setEffect(String name);

    public abstract void resetEffect();

    public abstract String currentEffectName();

    public abstract void enableVirtualBackground(boolean enabled);

    public abstract void setVirtualBackground(String name);

    public abstract void enableBlurBackground(boolean enabled);

    public abstract void setBlurStrength(int value);

    public abstract void enableLipsColor(boolean enabled);

    public abstract void setLipsColor(String rgba);

    public abstract void enableEyesColor(boolean enabled);

    public abstract void setEyesColor(String rgba);

    public abstract void enableHairColor(boolean enabled);

    public abstract void setHairColor(String rgba);

    public abstract void onDataUpdate(String json);
}
