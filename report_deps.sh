#!/bin/zsh
# ./report_deps.sh >> foo.log
./gradlew :app:dependencies -q --configuration implementationDependenciesMetadata
./gradlew :app:dependencies -q --configuration releaseCompileClasspath
./gradlew :app:dependencies -q --configuration releaseRuntimeClasspath