package com.benjaminsproule.swagger.gradleplugin.model

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import groovy.transform.ToString
import io.swagger.annotations.Contact
import io.swagger.annotations.Info
import io.swagger.annotations.License
import io.swagger.annotations.SwaggerDefinition
import io.swagger.models.Scheme
import io.swagger.models.Swagger
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.springframework.core.annotation.AnnotationUtils

@ToString(includeNames = true)
class ApiSourceExtension implements ModelValidator, Swagerable<Swagger> {
    Project project
    InfoExtension info
    SecurityDefinitionExtension securityDefinition
    List<String> locations
    List<String> schemes // Values MUST be from the list: "http", "https", "ws", "wss"
    List<String> outputFormats
    String basePath
    String host
    String templatePath
    String outputPath
    String swaggerDirectory
    String swaggerFileName = "swagger"
    String modelSubstitute
    String swaggerInternalFilter
    String swaggerApiReader
    boolean springmvc
    boolean useJAXBAnnotationProcessor = false
    boolean useJAXBAnnotationProcessorAsPrimary = true
    boolean jsonExampleValues = false
    boolean attachSwaggerArtifact
    File descriptionFile
    List<String> swaggerExtensions
    Set<String> typesToSkip = new HashSet<String>()
    List<String> apiModelPropertyAccessExclusions = new ArrayList<String>()
    List<String> typesToSkipList
    List<String> modelConverters
    List<String> apiModelPropertyAccessExclusionsList
    String excludePattern = '.*\\.pom'

    ApiSourceExtension(Project project) {
        this.project = project

        if (this.apiModelPropertyAccessExclusionsList != null) {
            this.apiModelPropertyAccessExclusions.addAll(this.apiModelPropertyAccessExclusionsList)
        }

        if (this.typesToSkipList != null) {
            this.typesToSkip.addAll(this.typesToSkipList)
        }
    }

    void info(Closure closure) {
        info = project.configure(new InfoExtension(project), closure) as InfoExtension
    }

    void securityDefinition(Closure closure) {
        securityDefinition = project.configure(new SecurityDefinitionExtension(), closure) as SecurityDefinitionExtension
    }

    @Override
    List<String> isValid() {
        if (!locations) {
            return ['locations required, specify classes or packages where swagger annotated classes are located']
        }

        //Order is important because if we have to search annotations we are going to
        //use the locations in the classpath search
        if (!info) {
            info = setInfoFromAnnotation()
            if (!info) {
                return ['Info is required by the swagger spec.']
            }
        }


        def errors = info.isValid()

        if (securityDefinition) {
            errors.addAll(securityDefinition.isValid())
        }

        return errors
    }

    @Override
    Swagger asSwaggerType() {
        def swagger = new Swagger()
        swagger.setHost(host ?: setHostFromAnnotation())
        swagger.setBasePath(basePath ?: setBasePathFromAnnotation())
        swagger.setInfo(info.asSwaggerType())

        if (schemes) {
            for (String scheme : schemes) {
                swagger.scheme(Scheme.forValue(scheme))
            }
        }

        // read description from file
        if (descriptionFile) {
            try {
                swagger.getInfo().setDescription(descriptionFile.getText().trim())
            } catch (IOException e) {
                throw new GradleException(e.getMessage(), e)
            }
        }

        if (securityDefinition) {
            swagger.setSecurityDefinitions(securityDefinition.asSwaggerType())
        }

        return swagger
    }

    private String setHostFromAnnotation() {
        for (Class<?> aClass : ClassFinder.instance().getValidClasses(SwaggerDefinition.class, locations, excludePattern)) {
            SwaggerDefinition swaggerDefinition = AnnotationUtils.findAnnotation(aClass, SwaggerDefinition.class)
            return swaggerDefinition.host()
        }
    }

    private String setBasePathFromAnnotation() {
        for (Class<?> aClass : ClassFinder.instance().getValidClasses(SwaggerDefinition.class, locations, excludePattern)) {
            SwaggerDefinition swaggerDefinition = AnnotationUtils.findAnnotation(aClass, SwaggerDefinition.class)
            return swaggerDefinition.basePath()
        }
    }

    private InfoExtension setInfoFromAnnotation() {
        def resultInfo = new InfoExtension(project)
        for (Class<?> aClass : ClassFinder.instance().getValidClasses(SwaggerDefinition, locations, excludePattern)) {
            SwaggerDefinition swaggerDefinition = AnnotationUtils.findAnnotation(aClass, SwaggerDefinition.class)
            Info infoAnnotation = swaggerDefinition.info()
            def info = new InfoExtension(project)
            info.title = infoAnnotation.title()
            info.description = infoAnnotation.description()
            info.version = infoAnnotation.version()
            info.termsOfService = infoAnnotation.termsOfService()
            info.license = from(infoAnnotation.license())
            info.contact = from(infoAnnotation.contact())
            resultInfo.mergeWith(info)
        }
        return resultInfo
    }

    private static ContactExtension from(Contact contactAnnotation) {
        return new ContactExtension(
            name: contactAnnotation.name(),
            email: contactAnnotation.email(),
            url: contactAnnotation.url())
    }

    private static LicenseExtension from(License licenseAnnotation) {
        return new LicenseExtension(
            name: licenseAnnotation.name(),
            url: licenseAnnotation.url())
    }
}
