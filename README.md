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
3. 执行脚本进行验证
   a. 如果需要升级 compose 版本或者 kotlin 版本，需先更新下 `snapshotDependencies` 的结果
   b. 然后再检查 can_upgrade 的结果，尝试升级，并执行 `validateDependencies` 来确保没有问题。
4. 一些三方库单独的升级
5. 最后跑一下 debug app 来确保没有问题

### 脚本使用

更新当前的校验规则，然后再执行检查

```shell
./gradlew -q :app:validateDependencies
```

确认所有检查通过后，执行导出快照，作为下一个版本的参考

```shell
./gradlew -q :app:snapshotDependencies
```