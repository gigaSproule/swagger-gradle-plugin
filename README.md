# Swagger Gradle Plugin [![Build Status](https://travis-ci.org/gigaSproule/swagger-gradle-plugin.png)](https://travis-ci.org/gigaSproule/swagger-gradle-plugin)  [ ![Download](https://api.bintray.com/packages/gigasproule/maven/swagger-gradle-plugin/images/download.svg) ](https://bintray.com/gigasproule/maven/swagger-gradle-plugin/_latestVersion)

This plugin was based on [kongchen's swagger-maven-plugin](https://github.com/kongchen/swagger-maven-plugin)

This enables your Swagger-annotated project to generate **Swagger specs** and **customizable, templated static documents** during the gradle build phase. Unlike swagger-core, swagger-gradle-plugin does not actively serve the spec with the rest of the application; it generates the spec as a build artifact to be used in downstream Swagger tooling.

# Features

* Supports [Swagger Spec 2.0](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md)
* Supports [SpringMvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) & [JAX-RS](https://jax-rs-spec.java.net/)
* Use [Handlebars](http://handlebarsjs.com/) as template to customize the static document.

# Usage
Import the plugin in your project by adding following configuration: 

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.benjaminsproule:swagger-gradle-plugin:+'
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

# Configuration for `swagger`

| **name** | **description** |
|----------|-----------------|
| `skipSwaggerGeneration` | If `true`, swagger generation will be skipped. Default is `false`. User property is `swagger.skip` |
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
| `templatePath` | The path of a [handlebars](http://handlebarsjs.com/) template file, see more details [below](#templatefile).|
| `outputPath` | The path of the generated static document, not existed parent directories will be created. If you don't want to generate a static document, just don't set it. |
| `outputFormats` | The format types of the generated swagger spec. Valid values are `json`, `yaml` or both `json,yaml`. The `json` format is default.|
| `swaggerDirectory` | The directory of generated `swagger.json` file. If null, no `swagger.json` will be generated. |
| `swaggerFileName` | The filename of generated filename.json file. If null, `swagger.json` will be generated. |
| `swaggerApiReader` | If not null, the value should be a full name of the class implementing `com.github.kongchen.swagger.docgen.reader.ClassSwaggerReader`. This allows you to flexibly implement/override the reader's implementation. Default is `com.github.kongchen.swagger.docgen.reader.JaxrsReader` |
| `attachSwaggerArtifact` | If enabled, the generated `swagger.json` file will be attached as a gradle artifact. The `swaggerDirectory`'s name will be used as an artifact classifier. Default is `false`. |
| `modelSubstitute` | The model substitute file's path, see more details [below](#model-substitution)|
| `typesToSkip` | Nodes of class names to explicitly skip during parameter processing. More details [below](#typesToSkip)|
| `apiModelPropertyAccessExclusionsList` | Allows the exclusion of specified `@ApiModelProperty` fields. This can be used to hide certain model properties from the swagger spec. More details [below](#apiModelPropertyAccessExclusionsList)|
| `jsonExampleValues` | If `true`, all example values in `@ApiModelProperty` will be handled as json raw values. This is useful for creating valid examples in the generated json for all property types, including non-string ones. |
| `modelConverters` | List of custom implementations of `io.swagger.converter.ModelConverter` that should be used when generating the swagger files. |
| `excludePattern` | Regex of files that will be excluded from the swagger documentation. The default is `.*\\.pom` so it ignores all pom files. |

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

You can define a `basic` definition like this:

```groovy
securityDefinition {
    name = 'MyBasicAuth'
    type = 'basic'
}
```

or define several definitions in a json file and specify the json path like this:

```groovy
securityDefinition {
    json = '/securityDefinition.json'
}
```
The file will be read by `getClass().getResourceAsStream`, so please note the path you configured.

Alternatively, specify the __absolute__ file path to the json definition file: 

```groovy
securityDefinition {
    jsonPath = "${basedir}/securityDefinition.json"
}
```

The `securityDefinition.json` file should also follow the spec, one sample file like this:

```js
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
# <a id="modelSubstitute">Model Substitution</a>
Throughout the course of working with Swagger, you may find that you need to substitute non-primitive objects for primitive objects. This is called model substituion, and it is supported by swagger-gradle-plugin. In order to configure model substitution, you'll need to create a model substitute file. This file is a simple text file containing `n` lines, where each line tells swagger-gradle-plugin to substitutes a model class with the supplied substitute. These two classes should be seperated by a colone (`:`).

## Sample model substitution

```
com.foo.bar.PetName : java.lang.String
```

The above model substitution configuration would tell the plugin to substitute `com.foo.bar.PetName` with `java.lang.String`.  As a result, the generated `swagger.json` would look like this ...

```js
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

```js
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
}
```

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

# Install/Deploy `swagger.json`

You can instruct `swagger-gradle-plugin` to deploy the generated `swagger.json` by adding the following to your build.gradle:

```groovy
swaggerDirectory = "${project.rootDir}/swagger-ui"
attachSwaggerArtifact = true

```
The above setting attaches the generated file to Gradle for install/deploy purpose with `swagger-ui`as classifier and `json` as type


# Example

```groovy
buildscript {
    repositories {
        mavenLocal()
        jcenter()
        ...
    }
    dependencies {
        classpath 'com.benjaminsproule:swagger-gradle-plugin:0.1.0'
    }
}
apply plugin: 'com.benjaminsproule.swagger'

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
            <!-- use markdown here because I'm using markdown for output,
            if you need to use html or other markup language, you need to use your target language -->
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
            json = '/securityDefinition.json'
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
        // attachSwaggerArtifact = true - WILL BE ADDED IN THE FUTURE
    }
}
...
```
