#!/bin/zsh
# ./report_deps.sh >> foo.log
./gradlew :app:dependencies --configuration implementationDependenciesMetadata
./gradlew :app:dependencies --configuration releaseCompileClasspath
./gradlew :app:dependencies --configuration releaseRuntimeClasspath