## Debug sample
当前 `debug` 分支主要用来验证依赖的可用性，**永远** 都不应该合入到 main 分支上

### 依赖之间的关系
- ksp 依赖 kotlin
- agp 依赖 gradle
  - 最低所需 Gradle 版本：https://developer.android.com/build/releases/gradle-plugin#updating-gradle
- compose 依赖 kotlin
  - Kotlin 2.0 之前：https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility
  - 从 2.0 开始改用独立的 Plugin，Compose Compiler Plugin 合入到 Kotlin 里了，随着 Kotlin 版本一起发布。
- target 版本对 Gradle 版本也有要求：https://developer.android.com/build/releases/gradle-plugin#api-level-support

### SOP
1. https://developer.android.com/develop/ui/compose/bom/bom-mapping 找到下一个版本号，确定 Compose 版本
   a. 调整 accompanist 版本
   b. 调整 compose-compiler 版本
2. 确定 Kotlin 版本，不一定要升级，一旦升级的话，对应的版本都要改动，Kotlin 版本必须一致
   a. Kotlin 版本只考虑 .0 和 .10 版本
3. 一些三方库单独的升级