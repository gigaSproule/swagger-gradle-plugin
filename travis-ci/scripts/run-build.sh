#!/usr/bin/env bash
set -ev
./gradlew clean check
older_gradle_versions=()
if [[ "${TRAVIS_JDK_VERSION}" == "oraclejdk10" || "${TRAVIS_JDK_VERSION}" == "openjdk10" ]]; then
    older_gradle_versions+=(4.2.1)
elif [[ "${TRAVIS_JDK_VERSION}" == "openjdk8" || "${TRAVIS_JDK_VERSION}" == "oraclejdk8" ]]; then
    older_gradle_versions+=(3.2 3.5.1 4.0)
fi
for gradle_version in ${older_gradle_versions[@]} ; do
    ./gradlew clean check -Dtest.gradleVersion=${gradle_version}
done
./gradlew clean install
./gradlew -b sample/groovy-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/groovy-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/java-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/java-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/kotlin-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/kotlin-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/scala-spring-boot-jaxrs/build.gradle clean generateSwaggerDocumentation
./gradlew -b sample/scala-spring-boot-mvc/build.gradle clean generateSwaggerDocumentation
