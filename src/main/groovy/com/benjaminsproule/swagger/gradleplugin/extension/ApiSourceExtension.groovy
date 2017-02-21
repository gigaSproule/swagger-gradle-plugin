package com.benjaminsproule.swagger.gradleplugin.extension

import com.github.kongchen.swagger.docgen.mavenplugin.ApiSource
import com.github.kongchen.swagger.docgen.mavenplugin.SecurityDefinition
import groovy.transform.ToString
import io.swagger.models.Contact
import io.swagger.models.Info
import io.swagger.models.License
import org.gradle.api.Project
import org.reflections.Reflections

import java.lang.annotation.Annotation

@ToString(includeNames = true)
class ApiSourceExtension extends ApiSource {
    private Project project
    List<String> apiModelPropertyAccessExclusionsList
    List<String> typesToSkipList
    boolean attachSwaggerArtifact

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
        InfoExtension infoExtension = project.configure(new InfoExtension(project), closure) as InfoExtension
        Info info = new Info()
        info.setDescription(infoExtension.description)
        info.setTermsOfService(infoExtension.termsOfService)
        info.setVersion(infoExtension.version)
        info.setTitle(infoExtension.title)

        if (infoExtension.contactExtension != null) {
            Contact contact = new Contact()
            contact.setName(infoExtension.contactExtension.name)
            contact.setUrl(infoExtension.contactExtension.url)
            contact.setEmail(infoExtension.contactExtension.email)
            info.setContact(contact)
        }

        if (infoExtension.licenseExtension != null) {
            License license = new License()
            license.setName(infoExtension.licenseExtension.name)
            license.setUrl(infoExtension.licenseExtension.url)
            info.setLicense(license)
        }
        this.info = info
    }

    void securityDefinition(Closure closure) {
        SecurityDefinitionExtension securityDefinitionExtension = project.configure(new SecurityDefinitionExtension(), closure) as SecurityDefinitionExtension
        SecurityDefinition securityDefinition = new SecurityDefinition();
        securityDefinition.setName(securityDefinitionExtension.name)
        securityDefinition.setType(securityDefinitionExtension.type)
        securityDefinition.setJson(securityDefinitionExtension.json)

        if (this.securityDefinitions == null) {
            this.securityDefinitions = new ArrayList<>()
        }
        this.securityDefinitions.add(securityDefinition)
    }

    private ClassLoader prepareClassLoader() {
        List<URL> urls = new ArrayList<>();
        project.configurations.runtime.resolve().each {
            if (!it.name.endsWith('.pom')) {
                urls.add(it.toURI().toURL())
            }
        }
        urls.add(project.sourceSets.main.output.classesDir.toURI().toURL())
        return new URLClassLoader(urls as URL[], this.getClass().getClassLoader())
    }

    @Override
    Set<Class<?>> getValidClasses(Class<? extends Annotation> clazz) {
        Set<Class<?>> classes = new HashSet<Class<?>>()
        ClassLoader classLoader = prepareClassLoader()
        if (getLocations() == null) {
            Set<Class<?>> c = new Reflections(classLoader, '').getTypesAnnotatedWith(clazz)
            classes.addAll(c)
        } else {
            if (locations.contains('')) {
                String[] sources = locations.split('')
                for (String source : sources) {
                    Set<Class<?>> c = new Reflections(classLoader, source).getTypesAnnotatedWith(clazz)
                    classes.addAll(c)
                }
            } else {
                classes.addAll(new Reflections(classLoader, locations).getTypesAnnotatedWith(clazz))
            }
        }

        return classes
    }
}
