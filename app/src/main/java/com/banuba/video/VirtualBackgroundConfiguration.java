package com.banuba.video;

public final class VirtualBackgroundConfiguration {

    public static final String DID_UPDATE_CONFIG = "bnb_did_update_config";


    /*package*/ final boolean useBlur;

    /*package*/ final Integer blurValue;

    /*package*/ final boolean useImage;

    /*package*/ final String imageName;

    public VirtualBackgroundConfiguration(
            boolean useBlur,
            Integer blurValue,
            boolean useImage,
            String imageName) {
        this.useBlur = useBlur;
        this.blurValue = blurValue;
        this.useImage = useImage;
        this.imageName = imageName;
    }

    public boolean getUseBlur() {
        return useBlur;
    }

    public Integer getBlurValue() {
        return blurValue;
    }

    public boolean getUseImage() {
        return useImage;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public String toString() {
        return "VirtualBackgroundConfiguration{" +
                "useBlur=" + useBlur +
                "," + "blurValue=" + blurValue +
                "," + "useImage=" + useImage +
                "," + "imageName=" + imageName +
        "}";
    }

}
