// In the asset pack's build.gradle.kts file:
plugins {
    id("com.android.asset-pack") version libs.versions.agp.get()
}

assetPack {
    packName.set("foo") // Directory name for the asset pack
    dynamicDelivery {
        // fast-follow：Google Play 在安装应用程序后自动开始下载 fast-follow 资源包。但是，应用程序首次启动时可能并非所有 fast-follow 资源包都可用。要检查状态并下载 fast-follow 资源包，请参见下文。
        // on-demand：Google Play 不会自动下载 on-demand 资源包。您必须手动开始下载。
        deliveryType.set("on-demand")
    }
}