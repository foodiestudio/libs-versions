// In the asset pack's build.gradle.kts file:
plugins {
    id("com.android.asset-pack") version libs.versions.agp.get()
}

assetPack {
    packName.set("foo") // Directory name for the asset pack
    dynamicDelivery {
        deliveryType.set("on-demand")
        // 如果使用这个默认安装的话，就不能用 AssetPackManager 相关 API 去检查
//        deliveryType.set("install-time")
    }
}