package com.benjaminsproule.swagger.gradleplugin

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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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

    def 'Should fail task if apiKey security config provided but keyLocation not provided'() {
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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

    def 'Should fail task if multiple security configs provided with one apiKey is valid but the other has keyLocation missing'() {
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        type = 'apiKey'
                        name = 'FirstApiKeyAuthentication'
                        keyLocation = 'header'
                    }
                    securityDefinition {
                        type = 'apiKey'
                        name = 'SecondApiKeyAuthentication'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'OAuth2Authentication'
                        type = 'oauth2'
                        tokenUrl = 'tokenUrl'
                        flow = 'implicit'
                        scope {
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'OAuth2Authentication'
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

    def 'Should fail task if oauth2 security config provided but scope name not provided'() {
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'OAuth2Authentication'
                        type = 'oauth2'
                        authorizationUrl = 'authorizationUrl'
                        flow = 'accessCode'
                        scope {
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

    def 'Should fail task if oauth2 security config provided but scope description not provided'() {
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'OAuth2Authentication'
                        type = 'oauth2'
                        authorizationUrl = 'authorizationUrl'
                        flow = 'accessCode'
                        scope {
                            name = 'name'
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

    def 'Should fail task if oauth2 security config provided but one scope is valid and the other description not provided'() {
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'OAuth2Authentication'
                        type = 'oauth2'
                        authorizationUrl = 'authorizationUrl'
                        flow = 'accessCode'
                        scope {
                            name = 'name'
                            description = 'description'
                        }
                        scope {
                            name = 'name'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
                    host = 'localhost:8080'
                    basePath = '/'
                    securityDefinition {
                        name = 'OAuth2Authentication'
                        type = 'oauth2'
                        authorizationUrl = 'authorizationUrl'
                        flow = 'accessCode'
                        scope {
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
                    swaggerDirectory = '${testProjectOutputDirAsString}/swaggerui'
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
    
    def 'Should fail task if security contains key without securityDefinition with same name'() {
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
                        type = 'basic'
                    }
                    security = [ [ NonMatchingAuth : [] ] ]
                  
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
