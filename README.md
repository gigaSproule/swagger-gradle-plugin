# Swagger Gradle Plugin [![CircleCI](https://circleci.com/gh/gigaSproule/swagger-gradle-plugin.svg?style=svg)](https://circleci.com/gh/gigaSproule/swagger-gradle-plugin)   [![Download](https://api.bintray.com/packages/gigasproule/maven/swagger-gradle-plugin/images/download.svg)](https://bintray.com/gigasproule/maven/swagger-gradle-plugin/_latestVersion)

This plugin was based on [kongchen's swagger-maven-plugin](https://github.com/kongchen/swagger-maven-plugin)

This enables your Swagger-annotated project to generate **Swagger specs** and **customizable, templated static documents** during the gradle build phase. Unlike swagger-core, swagger-gradle-plugin does not actively serve the spec with the rest of the application; it generates the spec as a build artifact to be used in downstream Swagger tooling.

N.B This plugin is tested against the latest of each major Gradle version from 3.x onwards. The reason for 3.x, is that 3.2 was the first that supported Kotlin, therefore keeping tests simpler. This does _not_ mean that this plugin won't work with earlier versions, just your mileage may vary.

# Features

* Supports [Swagger Spec 2.0](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md)
* Supports [SpringMvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) & [JAX-RS](https://jax-rs-spec.java.net/)
* Use [Handlebars](http://handlebarsjs.com/) as template to customize the static document.

# Usage
Import the plugin in your project by adding following configuration: 

## Gradle version >= 2.1
### build.gradle (Groovy DSL)
```groovy
plugins {
    id 'com.benjaminsproule.swagger' version '1.0.8'
}
```

### build.gradle.kts (Kotlin DSL)
```kotlin
plugins {
    id("com.benjaminsproule.swagger") version "1.0.8"
}
```
## Gradle versions < 2.1
```groovy
buildscript {
  repositories {
    maven {
      url 'https://plugins.gradle.org/m2/'
    }
  }
  dependencies {
    classpath 'gradle.plugin.com.benjaminsproule:swagger-gradle-plugin:1.0.0'
  }
}

apply plugin: 'com.benjaminsproule.swagger'

swagger {
    apiSource {
        ...
    }
}
```
One `apiSource` can be considered as a version of APIs of your service.

You can specify several `apiSource`s. Generally, one is enough.

```groovy
swagger {
    apiSource {
        ...
    }
    apiSource {
        ...
    }
}
```

# Configuration for `swagger`

| **name** | **description** |
|----------|-----------------|
| `apiSources` | List of `apiSource` closures. One `apiSource` can be considered as a version of APIs of your service. You can specify several `apiSource` closures, though generally one is enough. |

# Configuration for `apiSource`

| **name** | **description** |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `springmvc` | Tell the plugin your project is a JAX-RS(`false`) or a SpringMvc(`true`) project |
| `locations` **required**| Classes containing Swagger's annotation ```@Api```, or packages containing those classes can be configured here, using ```;``` as the delimiter. |
| `schemes` | The transfer protocol of the API. Values MUST be from the list: `"http"`, `"https"`, `"ws"`, `"wss"`. |
| `host` | The host (name or ip) serving the API. This MUST be the host only and does not include the scheme nor sub-paths. It MAY include a port.  The host does not support [path templating](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#pathTemplating).|
| `basePath` | The base path on which the API is served, which is relative to the host. The value MUST start with a leading slash (/). The basePath does not support [path templating](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#pathTemplating). |
| `descriptionFile` | A Path to file with description to be set to Swagger Spec 2.0's [info Object](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#infoObject) |
| `info` **required**| The basic information of the api, using same definition as Swagger Spec 2.0's [info Object](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#infoObject) |
| `securityDefinitions` | You can put your [security definitions](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#security-definitions-object) here, see more details [below](#securityDefinitions)|
| `security` | A declaration of which security schemes are applied for the API as a whole. [security requirement](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#securityRequirementObject) see more details [below](#security)|
| `templatePath` | The path of a [handlebars](http://handlebarsjs.com/) template file, see more details [below](#templatefile).|
| `outputPath` | The path of the generated static document, not existed parent directories will be created. If you don't want to generate a static document, just don't set it. |
| `outputFormats` | The format types of the generated swagger spec. Valid values are `json`, `yaml` or both (as a list, e.g. `['json']`). The `json` format is default.|
| `shouldSortArrays` | JSON arrays will be sorted instead of being written in the order of appearance. Default is `false`.|
| `swaggerDirectory` | The directory of generated `swagger.json` file. If null, no `swagger.json` will be generated. |
| `swaggerFileName` | The filename of generated filename.json file. If null, `swagger.json` will be generated. |
| `swaggerApiReader` | If not null, the value should be a full name of the class extending `com.github.kongchen.swagger.docgen.reader.ClassSwaggerReader`. This allows you to flexibly implement/override the reader's implementation. Default is `com.github.kongchen.swagger.docgen.reader.JaxrsReader`. More details [below](#swaggerApiReader)|
| `attachSwaggerArtifact` | If enabled, the generated `swagger.json` file will be attached as a gradle artifact. The `swaggerFileName` will be used as an artifact classifier. Default is `false`. |
| `modelSubstitute` | The model substitute file's path, see more details [below](#model-substitution)|
| `typesToSkip` | Nodes of class names to explicitly skip during parameter processing. More details [below](#typesToSkip)|
| `apiModelPropertyAccessExclusionsList` | Allows the exclusion of specified `@ApiModelProperty` fields. This can be used to hide certain model properties from the swagger spec. More details [below](#apiModelPropertyAccessExclusionsList)|
| `jsonExampleValues` | If `true`, all example values in `@ApiModelProperty` will be handled as json raw values. This is useful for creating valid examples in the generated json for all property types, including non-string ones. |
| `modelConverters` | List of custom implementations of `io.swagger.converter.ModelConverter` that should be used when generating the swagger files. More details [below](#modelConverters)|
| `excludePattern` | Regex of files that will be excluded from the swagger documentation. The default is `.*\\.pom` so it ignores all pom files. |
| `tagStrategy` | Default no. `class` use class name if no tags are set to group operations specific to controller. (currently only springmvc)  |
| `expandSuperTypes` | Default `true`. You can skip the scan of super types (parent class, interfarce)

# <a id="templatefile">Template File</a>

If you'd like to generate a template-driven static document, such as markdown or HTML documentation, you'll need to specify a [handlebars](https://github.com/jknack/handlebars.java) template file in ```templatePath```.
The value for ```templatePath``` supports 2 kinds of path:

1. Resource in classpath. You should specify a resource path with a **classpath:** prefix.
    e.g:

    1. **`classpath:/markdown.hbs`**
    1. **`classpath:/templates/hello.html`**

1. Local file's absolute path.
    e.g:

    1. **`${project.rootDir}/src/main/resources/markdown.hbs`**
    1. **`${project.rootDir}/src/main/resources/template/hello.html`**


There's a [standalone project](https://github.com/kongchen/api-doc-template) for the template files, fetch them and customize it for your own project.

# <a id="securityDefinitions">Security Definitions</a>

There're 3 types of security definitions according to Swagger Spec: `basic`, `apiKey` and `oauth2`.

You can define multi definitions here, but you should fully follow [the spec](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md#securitySchemeObject).

## Basic - Security Definitions
You can define a `basic` definition like this:

```groovy
securityDefinition {
    // `name` can be used refer to this security schemes from elsewhere
    name = 'MyBasicAuth'
    type = 'basic'
}
```

## ApiKey - Security Definitions
You can also define a `ApiKey` definition like this:

```groovy
swagger {
    apiSource {
        ...
        securityDefinition {
            // `name` can be used refer to this security schemes from elsewhere
            name = 'ApiKeyAuth'
            type = 'apiKey'
            // The location of the API key. Valid values are "query" or "header".
            keyLocation = 'header'
            // The name of the header
            keyName = 'X-API-Key'
        }
    }
}
```

## Oauth2 - Security Definitions
You can also define a `Oauth2` definition like this:

```groovy
swagger {
    apiSource {
        ...
        securityDefinition {
            // `name` can be used refer to this security schemes from elsewhere
            name = 'OAuth2Authentication'
            type = 'oauth2'
            // The flow used by the OAuth2 security scheme
            flow = 'accessCode'
            authorizationUrl = 'https://somewhere.com/authorization'
            tokenUrl = 'https://somewhere.com/token'
            scope {
                name = 'read:model'
                description = 'Read the details of the model'
            }
        }
    }
}
```

## Json - Security Definitions
It is also possible to define several definitions in a json file and specify the json path like this:

```groovy
securityDefinition {
    json = 'securityDefinition.json'
}
```

Alternatively, specify the __absolute__ file path to the json definition file:

```groovy
securityDefinition {
    json = "${project.projectDir}/securityDefinition.json"
}
```

The `securityDefinition.json` file should also follow the spec, one sample file like this:

```json
{
  "api_key": {
    "type": "apiKey",
    "name": "api_key",
    "in": "header"
  },
  "petstore_auth": {
    "type": "oauth2",
    "authorizationUrl": "http://swagger.io/api/oauth/dialog",
    "flow": "implicit",
    "scopes": {
      "write:pets": "modify pets in your account",
      "read:pets": "read your pets"
    }
  }
}
```

__Note:__ It is only possible to define the OAuth2 type in a json file and not directly in the gradle configuration.

# <a id="security">Security</a>

Allows to set a security requirement on the whole API. This can be done with multiple security requirements applied as AND or OR values. See https://swagger.io/docs/specification/2-0/authentication/
The key of the security requirement has to match a name from a securityDefinition.

```groovy
security = [ [ ApiKeyAuth : [] ] ]
```

Use BasicAuth with ApiKey or OAuth2 with given scopes 
(basic(name: MyBasicAuth) && apiKey(name: MyApiKey)) || oauth2(name: MyOAuth2)

```groovy
security = [ [ MyBasicAuth : [], MyApiKey : [] ], [ MyOAuth2 : [ 'scope1', 'scope2' ] ] ]
```


# <a id="swaggerApiReader">Swagger API Reader</a>
You can instruct `swagger-gradle-plugin` to use a custom swagger api reader rather than use the default by adding the following to your build.gradle:
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.custom:swagger-api-reader:1.0.0'
    }
}
...
swagger {
    apiSource {
        ...
        swaggerApiReader = [ 'com.custom.swagger.ApiReader' ]
    }
}
```

It is important to note that the class has to be available in the buildscript's classpath.

# <a id="modelSubstitute">Model Substitution</a>
Throughout the course of working with Swagger, you may find that you need to substitute non-primitive objects for primitive objects. This is called model substituion, and it is supported by swagger-gradle-plugin. In order to configure model substitution, you'll need to create a model substitute file. This file is a simple text file containing `n` lines, where each line tells swagger-gradle-plugin to substitutes a model class with the supplied substitute. These two classes should be seperated by a colone (`:`).

## Sample model substitution

```
com.foo.bar.PetName : java.lang.String
```

The above model substitution configuration would tell the plugin to substitute `com.foo.bar.PetName` with `java.lang.String`.  As a result, the generated `swagger.json` would look like this ...

```json
 "definitions" : {
    "Pet" : {
      "properties" : {
        ...
        "petName" : {
          "type" : "string"
        }
        ...
      }
    }
```
... instead of like this:

```json
 "definitions" : {
    "Pet" : {
      "properties" : {
        ...
        "petName" : {
          "$ref" : "#/definitions/PetName"
        }
        ...
      }
    }
```

The model substitution file will be read by `getClass().getResourceAsStream`, so please note the path you configured.

# <a id="typesToSkip">Skipping Types During Processing with `typesToSkip`</a>

You can instruct `swagger-gradle-plugin` to skip processing the parameters of certain types by adding the following to your build.gradle:
// TODO: Not fully supported yet
```groovy
typesToSkip = [
    'com.foobar.skipper.SkipThisClassPlease',
    'com.foobar.skipper.AlsoSkipThisClassPlease'
]
```

# <a id="modelConverters">Model Converters</a>

You can instruct `swagger-gradle-plugin` to use a custom model converter rather than use the default by adding the following to your build.gradle:
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.custom:model-converter:1.0.0'
    }
}
...
swagger {
    apiSource {
        ...
        modelConverters = [ 'com.custom.model.Converter' ]
    }
}
```

It is important to note that the class has to be available in the buildscript's classpath.

# <a id="apiModelPropertyAccessExclusionsList">Excluding certain `@ApiModelProperty` items</a>
If you'd like to exclude certain `@ApiModelProperty`s based on their `access` values, you may do so by adding the following as a child node of `apiSource` in your build.gradle:

```groovy
apiModelPropertyAccessExclusionsList = [
    'secret-property'
]
```

The above setting would prevent `internalThing` from appearing in the swagger spec output, given this annotated model:

```java
...
    @ApiModelProperty(name = "internalThing", access = "secret-property")
    public String getInternalThing() {
        return internalThing;
    }
...
```

Note: In order to use `apiModelPropertyAccessExclusionsList`, you must specify both the `name` and `access` fields of the property you wish to exclude.

# Generating the swagger documentation
To generate the swagger documentation, you need to run `./gradlew generateSwaggerDocumentation`.

_N.B_ In previous versions (< 0.1.0) the task was `swagger`, but this caused a conflict with another plugin, `swagger-codegen`, (issue #8).

## Skipping generating the swagger documentation
To skip generating the swagger documentation, you need to include the property `swagger.skip` (e.g. `./gradlew clean build -Pswagger.skip`)

# Install/Deploy `swagger.json`

You can instruct `swagger-gradle-plugin` to deploy the generated `swagger.json` by adding the following to your build.gradle:

```groovy
attachSwaggerArtifact = true
```
The above setting attaches the generated file to Gradle for install/deploy purpose with `swaggerDirectory`'s name as classifier and the `outputFormat` as type.

Please note that when using the `maven-publish` plugin instead of the `maven` plugin, the classifier _must_ be specified in the configuration as it uses a different mechanism for the classifier. This is especially important when using multiple `apiSource` closures. Example:
```groovy
publishing {
    publications {
        maven(MavenPublication) {
            artifact source: "${swaggerDirectory}/publicApiSwagger.json", classifier: 'publicApiSwagger'
            artifact source: "${swaggerDirectory}/privateApiSwagger.json", classifier: 'privateApiSwagger'
        }
    }
    repositories {
        maven {
            url "https://path/to/repo"
        }
    }
}
swagger {
     apiSource {
         attachSwaggerArtifact = true
         locations = ['com.benjaminsproule.public']
         swaggerDirectory = "${swaggerDirectory}"
         swaggerFileName = 'publicSwagger'
     }
     apiSource {
         attachSwaggerArtifact = true
         locations = ['com.benjaminsproule.private']
         swaggerDirectory = "${swaggerDirectory}"
         swaggerFileName = 'privateSwagger'
     }
}
```


# Example

## build.gradle (Groovy DSL)

```groovy
plugins {
    id "com.benjaminsproule.swagger" version "1.0.0"
}

swagger {
    apiSource {
        springmvc = true
        locations = [ 'com.wordnik.swagger.sample' ]
        schemes = [ 'http', 'https' ]
        host = 'www.example.com:8080'
        basePath = '/api'
        info {
            title = 'Swagger Gradle Plugin Sample'
            version = 'v1'
            // use markdown here because I'm using markdown for output,
            // if you need to use html or other markup language, you need to use your target language
            description = 'This is a sample.'
            termsOfService = 'http://www.example.com/termsOfService'
            contact {
                email = 'email@email.com'
                name = 'Name'
                url = 'http://www.example.com'
            }
            license {
                url = 'http://www.apache.org/licenses/LICENSE-2.0.html'
                name = 'Apache 2.0'
            }
        }
        securityDefinition {
            name = 'basicAuth'
            type = 'basic'
        }
        securityDefinition {
            json = 'securityDefinition.json'
        }
        /**
            Support classpath or file absolute path here.
            1) classpath e.g: "classpath:/markdown.hbs", "classpath:/templates/hello.html"
            2) file e.g: "${project.rootDir}/src/main/resources/markdown.hbs", "${project.rootDir}/src/main/resources/template/hello.html"
        **/
        templatePath = "${project.rootDir}/src/test/resources/strapdown.html.hbs"
        outputPath = "${project.rootDir}/generated/document.html"
        swaggerDirectory = "${project.rootDir}/generated/swagger-ui"
        swaggerApiReader = 'com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader'
        modelConverters = [ 'io.swagger.validator.BeanValidator' ]
        attachSwaggerArtifact = true
    }
}
```

## build.gradle.kts (Kotlin DSL)

```kotlin
plugins {
    id("com.benjaminsproule.swagger") version "1.0.0"
}

swagger {
    apiSource(closureOf<ApiSourceExtension> {
        springmvc = true
        locations = listOf("com.wordnik.swagger.sample"]
        schemes = listOf("http", "https")
        host = "www.example.com:8080"
        basePath = "/api"
        info(closureOf<InfoExtension> {
            title = "Swagger Gradle Plugin Sample"
            version = "v1"
            // use markdown here because I"m using markdown for output,
            // if you need to use html or other markup language, you need to use your target language
            description = "This is a sample."
            termsOfService = "http://www.example.com/termsOfService"
            contact(closureOf<ContactExtension> {
                email = "email@email.com"
                name = "Name"
                url = "http://www.example.com"
            })
            license(closureOf<LicenseExtension> {
                url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                name = "Apache 2.0"
            })
        })
        securityDefinition(closureOf<SecurityDefinitionExtension> {
            name = "basicAuth"
            type = "basic"
        })
        securityDefinition(closureOf<SecurityDefinitionExtension> {
            json = "securityDefinition.json"
        }
        /**
            Support classpath or file absolute path here.
            1) classpath e.g: "classpath:/markdown.hbs", "classpath:/templates/hello.html"
            2) file e.g: "${project.rootDir}/src/main/resources/markdown.hbs", "${project.rootDir}/src/main/resources/template/hello.html"
        **/
        templatePath = "${project.rootDir}/src/test/resources/strapdown.html.hbs"
        outputPath = "${project.rootDir}/generated/document.html"
        swaggerDirectory = "${project.rootDir}/generated/swagger-ui"
        swaggerApiReader = "com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader"
        modelConverters = listOf("io.swagger.validator.BeanValidator")
        attachSwaggerArtifact = true
    })
}
```
