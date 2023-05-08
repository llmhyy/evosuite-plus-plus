package org.evosuite.testcase.parser;

import org.evosuite.TestGenerationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;

public class ParserUtil {

    private static final Logger logger = LoggerFactory.getLogger(ParserUtil.class);

    public static Class<?> loadClass(String className) {
        if (Objects.equals(className, "ArrayList")) {
            className = "java.util.ArrayList";
        } else if (Objects.equals(className, "ObjectUtils")) {
            className = "framework.util.ObjectUtils";
        }
        Class<?> clazz;
        try {
            String fullyQualifiedName = findFullyQualifiedName(className);
            clazz = Class.forName(fullyQualifiedName);
        } catch (ClassNotFoundException e1) {
            try {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(className);
            } catch (ClassNotFoundException e2) {
                logger.error(e2.getMessage());
                System.out.println(className);
                clazz = null;
            }
        }
        return clazz;
    }

    private static String findFullyQualifiedName(String className) throws ClassNotFoundException {
        String classFilePath = className.replace(".", "/") + ".class";
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL resource = classLoader.getResource(classFilePath);
        if (resource == null) {
            throw new ClassNotFoundException("Class " + className + " not found on classpath.");
        }
        String url = resource.toString();
        int packageIndex = url.lastIndexOf(className.replace(".", "/"));
        String packageName = url.substring(0, packageIndex);
        packageName = packageName.substring(packageName.lastIndexOf("/") + 1);
        return packageName + "." + className;
    }
}
