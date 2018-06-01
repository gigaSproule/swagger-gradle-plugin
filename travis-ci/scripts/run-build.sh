#!/usr/bin/env bash
set -ev
./gradlew clean check
if [[ "${TRAVIS_JDK_VERSION}" == "openjdk8" || "${TRAVIS_JDK_VERSION}" == "oraclejdk8" || "${TRAVIS_JDK_VERSION}" == "oraclejdk9" ]]; then
    ./gradlew clean check -Dtest.gradleVersion=4.0
    ./gradlew clean check -Dtest.gradleVersion=3.0
fi
./gradlew clean install
./gradlew -b sample/groovy-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/groovy-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/java-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/java-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/kotlin-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/kotlin-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
