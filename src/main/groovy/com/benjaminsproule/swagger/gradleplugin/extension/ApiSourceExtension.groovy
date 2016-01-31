package com.benjaminsproule.swagger.gradleplugin.extension

import com.github.kongchen.swagger.docgen.GenerateException
import com.github.kongchen.swagger.docgen.mavenplugin.ApiSource
import com.github.kongchen.swagger.docgen.mavenplugin.SecurityDefinition
import groovy.transform.ToString
import io.swagger.annotations.Api
import io.swagger.models.Contact
import io.swagger.models.Info
import io.swagger.models.License
import org.gradle.api.Project
import org.reflections.Reflections

@ToString(includeNames = true)
class ApiSourceExtension extends ApiSource {
    private Project project
    ClassLoader classLoader

    ApiSourceExtension(Project project) {
        this.project = project
        this.classLoader = prepareClassLoader()
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
        SecurityDefinition securityDefinition = new SecurityDefinition();
        securityDefinition.setName(closure.name)
        securityDefinition.setType(closure.type)
        securityDefinition.setJson(closure.json)
        if (this.securityDefinitions == null) {
            this.securityDefinitions = new ArrayList<>()
        }
        this.securityDefinitions.add(securityDefinition)
    }

    private ClassLoader prepareClassLoader() {
        List<URL> urls = project.configurations.runtime.resolve().collect { it.toURI().toURL() }
        urls.add(project.sourceSets.main.output.classesDir.toURI().toURL())
        return new URLClassLoader(urls as URL[], this.getClass().getClassLoader())
    }

    @Override
    Set<Class<?>> getValidClasses() throws GenerateException {
        Set<Class<?>> classes = new HashSet<Class<?>>()
        if (getLocations() == null) {
            Set<Class<?>> c = new Reflections(classLoader, "").getTypesAnnotatedWith(Api.class)
            classes.addAll(c)
        } else {
            if (locations.contains("")) {
                String[] sources = locations.split("")
                for (String source : sources) {
                    Set<Class<?>> c = new Reflections(classLoader, source).getTypesAnnotatedWith(Api.class)
                    classes.addAll(c)
                }
            } else {
                classes.addAll(new Reflections(classLoader, locations).getTypesAnnotatedWith(Api.class))
            }
        }

        return classes
    }
}
