package com.benjaminsproule.swagger.gradleplugin

import com.github.kongchen.swagger.docgen.GenerateException
import com.github.kongchen.swagger.docgen.mavenplugin.ApiSource
import groovy.transform.ToString
import io.swagger.annotations.Api
import io.swagger.models.Info
import org.gradle.api.Project
import org.reflections.Reflections

@ToString(includeNames = true)
class SwaggerPluginExtension extends ApiSource {
    private Project project
    ClassLoader classLoader;

    SwaggerPluginExtension(Project project) {
        this.project = project;
    }

    InfoExtension info(Closure closure) {
        InfoExtension infoExtension = project.configure(new InfoExtension(), closure) as InfoExtension
        Info info = new Info()
        info.setTitle(infoExtension.title)
        info.setVersion(infoExtension.version)
        info.setLicense(infoExtension.license)
        this.info = info
        return infoExtension
    }

    @Override
    public Set<Class<?>> getValidClasses() throws GenerateException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        if (getLocations() == null) {
            Set<Class<?>> c = new Reflections(classLoader, "").getTypesAnnotatedWith(Api.class);
            classes.addAll(c);
        } else {
            if (locations.contains(";")) {
                String[] sources = locations.split(";");
                for (String source : sources) {
                    Set<Class<?>> c = new Reflections(classLoader, source).getTypesAnnotatedWith(Api.class);
                    classes.addAll(c);
                }
            } else {
                classes.addAll(new Reflections(classLoader, locations).getTypesAnnotatedWith(Api.class));
            }
        }

        return classes;
    }
}
