// In the asset pack's build.gradle.kts file:
plugins {
    id("com.android.asset-pack") version libs.versions.agp.get()
}

assetPack {
    packName.set("haha") // Directory name for the asset pack
    dynamicDelivery {
        //  如果使用这个默认安装的话，这个 packName 就用不到了
        deliveryType.set("install-time")
    }
}