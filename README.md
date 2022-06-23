An example of banuba_sdk and WebRTC integration

> **Important**
>
> [master](../../tree/master) branch is always compatible with latest SDK version. Please use [v0.x](../../tree/v0.x) branch for SDK version 0.x (e.g. v0.38).

# Getting Started

1. Get the latest Banuba SDK archive for Android and the client token. Please fill in our form on [form on banuba.com](https://www.banuba.com/face-filters-sdk) website, or contact us via [info@banuba.com](mailto:info@banuba.com).
2. Copy `aar` files from the Banuba SDK archive into `libs` dir:
   `banuba_sdk-release.aar` => [`BNBWebRTC/libs/`](../../tree/master/BNBWebRTC/libs)
   `banuba_effect_player-release.aar` => [`BNBWebRTC/libs/`](../../tree/master/BNBWebRTC/libs)
3. Copy and Paste your client token to `KEY` variable of [`app/src/main/java/com/banuba/BanubaClientToken.java`](../../tree/master/app/src/main/java/com/banuba/BanubaClientToken.java)
4. Open the project in Android Studio and run the necessary target using the usual steps.

### NOTE:
You can download test effects here: [Effects](https://docs.banuba.com/face-ar-sdk/overview/demo_face_filters)
Then put them in the `effects` dir: [`BNBWebRTC/src/main/assets/bnb-resources/effects`](../../tree/master/BNBWebRTC/src/main/assets/bnb-resources/effects) 
