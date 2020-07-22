package com.benjaminsproule.swagger.gradleplugin.generator

import com.benjaminsproule.swagger.gradleplugin.Utils
import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder
import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.io.FileTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import groovy.transform.ToString
import io.swagger.models.Swagger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset

class ApiDocGenerator implements Generator {
    private static final Logger LOG = LoggerFactory.getLogger(ApiDocGenerator)
    private ApiSourceExtension apiSourceExtension
    private ClassFinder classFinder
    private boolean isSorted = false

    ApiDocGenerator(ApiSourceExtension apiSourceExtension, ClassFinder classFinder) {
        this.apiSourceExtension = apiSourceExtension
        this.classFinder = classFinder
    }

    @Override
    void generate(Swagger source) {
        if (!isSorted) {
            Utils.sortSwagger(source)
            isSorted = true
        }
        LOG.info("Writing swagger doc to ${apiSourceExtension.outputPath}")

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(apiSourceExtension.outputPath)
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, Charset.forName("UTF-8"))

            TemplatePath tp = parseTemplateUrl(apiSourceExtension.templatePath)

            Handlebars handlebars = new Handlebars(tp.loader)
            initHandlebars(handlebars)

            Template template = handlebars.compile(tp.name)

            template.apply(source, writer)
            writer.close()
            LOG.info("Done!")
        } catch (MalformedURLException e) {
            throw new GenerateException(e)
        } catch (IOException e) {
            throw new GenerateException(e)
        }
    }

    private static void initHandlebars(Handlebars handlebars) {
        handlebars.registerHelper("ifeq", new Helper<String>() {
            @Override
            CharSequence apply(String value, Options options) throws IOException {
                if (value == null || options.param(0) == null) {
                    return options.inverse()
                }
                if (value == options.param(0)) {
                    return options.fn()
                }
                return options.inverse()
            }
        })

        handlebars.registerHelper("basename", new Helper<String>() {
            @Override
            CharSequence apply(String value, Options options) throws IOException {
                if (value == null) {
                    return null
                }
                int lastSlash = value.lastIndexOf("/")
                if (lastSlash == -1) {
                    return value
                } else {
                    return value.substring(lastSlash + 1)
                }
            }
        })

        handlebars.registerHelper(StringHelpers.join.name(), StringHelpers.join)
        handlebars.registerHelper(StringHelpers.lower.name(), StringHelpers.lower)
    }

    private TemplatePath parseTemplateUrl(String templatePath) throws GenerateException {
        if (templatePath == null) {
            return null
        }
        TemplatePath tp
        if (templatePath.startsWith(Utils.CLASSPATH)) {
            String resPath = templatePath.substring(Utils.CLASSPATH.length())
            if (resPath.startsWith('/')) {
                resPath = resPath.substring(1, resPath.length())
            }
            tp = extractTemplateObject(resPath)
            tp.loader = new ClassPathTemplateLoaderImpl(tp.prefix, tp.suffix, classFinder)
        } else {
            tp = extractTemplateObject(templatePath)
            tp.loader = new FileTemplateLoader(tp.prefix, tp.suffix)
        }

        return tp
    }

    private static TemplatePath extractTemplateObject(String resPath) throws GenerateException {
        TemplatePath tp = new TemplatePath()
        String prefix = TemplateLoader.DEFAULT_PREFIX
        String suffix = TemplateLoader.DEFAULT_SUFFIX
        String name = ""

        int prefixidx = resPath.lastIndexOf("/")
        if (prefixidx != -1) {
            prefix = resPath.substring(0, prefixidx + 1)
        }

        int extidx = resPath.lastIndexOf(".")
        if (extidx != -1) {
            suffix = resPath.substring(extidx)
            if (extidx < prefix.length()) {
                throw new GenerateException("You have an interesting template path:" + resPath)
            }
            name = resPath.substring(prefix.length(), extidx)
        }
        tp.name = name
        tp.prefix = prefix
        tp.suffix = suffix

        return tp
    }

    @ToString(includeNames = true)
    static class TemplatePath {
        String prefix
        String name
        String suffix
        public TemplateLoader loader
    }
}
