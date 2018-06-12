package com.benjaminsproule.swagger.gradleplugin

import spock.lang.Ignore

import static org.gradle.testkit.runner.TaskOutcome.FAILED

class ValidationITest extends AbstractPluginITest {

    def 'Should fail task if no locations provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { name = 'Apache 2.0' }
                        contact { name = 'Joe Blogs' }
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if neither info config nor annotations provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if info title not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if info version not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if license config provided but name not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                        license { url = 'license url' }
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if security config provided but type not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'MyBasicAuth'
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if apiKey security config provided but name not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        type = 'apiKey'
                        keyLocation = 'header'
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if apiKey security config provided but in not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        type = 'apiKey'
                        name = 'ApiKeyAuthentication'
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    @Ignore
    // TODO: To be fixed as part of https://github.com/gigaSproule/swagger-gradle-plugin/issues/43
    def 'Should fail task if oauth2 security config provided but authorizationUrl not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        type = 'oauth2'
                        tokenUrl = 'tokenUrl'
                        flow = 'implicit'
                        scopes = {
                            name = 'scope'
                            description = 'description'
                        }
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    @Ignore
    // TODO: To be fixed as part of https://github.com/gigaSproule/swagger-gradle-plugin/issues/43
    def 'Should fail task if oauth2 security config provided but scopes not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        type = 'oauth2'
                        authorizationUrl = 'authorizationUrl'
                        flow = 'accessCode'
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    @Ignore
    // TODO: To be fixed as part of https://github.com/gigaSproule/swagger-gradle-plugin/issues/43
    def 'Should fail task if accessCode oauth2 security config provided but tokenUrl not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        type = 'oauth2'
                        authorizationUrl = 'authorizationUrl'
                        flow = 'accessCode'
                        scopes = {
                            name = 'scope'
                            description = 'description'
                        }
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if tag config provided but name not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    tag {
                        description = 'tagDescription'
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }

    def 'Should fail task if tag external docs config provided but url not provided'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'groovy'
                id 'com.benjaminsproule.swagger'
            }

            swagger {
                apiSource {
                    locations = ['com.benjaminsproule.swager.gradleplugin.classpath']
                    schemes = ['http']
                    info {
                        title = 'test'
                        version = '1'
                    }
                    swaggerDirectory = '${testProjectOutputDir}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    tag {
                        name = 'tag'
                        description = 'tagDescription'
                        externalDocs {
                            description = 'externalDocsDescription'
                        }
                    }
                }
            }
        """

        when:
        def runResult
        try {
            runResult = runPluginTask()
        } catch (Exception e) {
            runResult = e.buildResult
        }

        then:
        runResult.task(":${GenerateSwaggerDocsTask.TASK_NAME}").outcome == FAILED
    }
}
