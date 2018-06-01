#!/usr/bin/env bash
set -ev
./gradlew clean check
if [[ "${TRAVIS_JDK_VERSION}" == "openjdk8" || "${TRAVIS_JDK_VERSION}" == "oraclejdk8" || "${TRAVIS_JDK_VERSION}" == "oraclejdk9" ]]; then
    older_gradle_versions=(3.2 3.5.1 4.0)
    for gradle_version in ${older_gradle_versions[@]} ; do
        ./gradlew clean check -Dtest.gradleVersion=${gradle_version}
    done
fi
./gradlew clean install
./gradlew -b sample/groovy-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/groovy-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/java-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/java-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/kotlin-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/kotlin-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
