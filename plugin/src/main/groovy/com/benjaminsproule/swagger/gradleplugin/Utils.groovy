package com.benjaminsproule.swagger.gradleplugin

import com.benjaminsproule.swagger.gradleplugin.exceptions.GenerateException
import io.swagger.models.*
import io.swagger.models.parameters.Parameter
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.RequestMapping

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class Utils {
    public static final String CLASSPATH = "classpath:"

    private static final HTTP_METHODS = ["Get", "Delete", "Post", "Put", "Options", "Patch"]

    /**
     * Extracts all routes from the annotated class
     *
     * @param controllerClazz
     *            Introspected class
     * @return At least 1 route value (empty string)
     */
    static String[] getControllerRequestMapping(Class<?> controllerClazz) {
        String[] controllerRequestMappingValues = []

        // Determine if we will use class-level requestmapping or dummy string
        RequestMapping classRequestMapping = AnnotationUtils.findAnnotation(controllerClazz, RequestMapping)
        if (classRequestMapping != null) {
            controllerRequestMappingValues = classRequestMapping.value()
        }

        if (controllerRequestMappingValues.length == 0) {
            controllerRequestMappingValues = new String[1]
            controllerRequestMappingValues[0] = ""
        }
        return controllerRequestMappingValues
    }

    static void sortSwagger(Swagger swagger, Boolean orderLists) throws GenerateException {
        if (swagger == null || swagger.getPaths() == null) {
            return
        }

        TreeMap<String, Path> sortedMap = new TreeMap<String, Path>()
        sortedMap.putAll(swagger.getPaths())
        swagger.paths(sortedMap)

        for (Path path : swagger.getPaths().values()) {
            for (String m : HTTP_METHODS) {
                sortResponses(path, m, orderLists)
            }
        }

        //reorder definitions
        if (swagger.getDefinitions() != null) {
            TreeMap<String, Model> defs = new TreeMap<String, Model>()
            defs.putAll(swagger.getDefinitions())
            swagger.setDefinitions(defs)
        }

        // order the tags
        if (orderLists && swagger.getTags() != null) {
            Collections.sort(swagger.getTags(), new Comparator<Tag>() {
                int compare(final Tag a, final Tag b) {
                    return a.toString().compareTo(b.toString())
                }
            })
        }

    }

    private static void sortResponses(Path path, String method, Boolean orderLists) throws GenerateException {
        try {
            Method m = Path.getDeclaredMethod("get" + method)
            Operation op = (Operation) m.invoke(path)
            if (op == null) {
                return
            }
            Map<String, Response> responses = op.getResponses()
            TreeMap<String, Response> res = new TreeMap<String, Response>()
            res.putAll(responses)
            if (orderLists) {
                op.getParameters().sort(new Comparator<Parameter>() {
                    @Override
                    int compare(Parameter o1, Parameter o2) {
                        return o1.getName() <=> o2.getName()
                    }
                })
            }
            op.setResponses(res)
        } catch (NoSuchMethodException e) {
            throw new GenerateException(e)
        } catch (InvocationTargetException e) {
            throw new GenerateException(e)
        } catch (IllegalAccessException e) {
            throw new GenerateException(e)
        }
    }
}
