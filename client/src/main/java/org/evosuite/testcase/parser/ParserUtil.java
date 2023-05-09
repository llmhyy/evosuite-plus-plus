package org.evosuite.testcase.parser;

import com.google.common.reflect.ClassPath;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.runtime.Reflection;
import org.evosuite.setup.TestClusterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ParserUtil {

    private static final List<Package> packages = initializePackages(false);
    private static final List<Package> javaPackages = initializePackages(true);
    private static final List<Class<?>> classes = initializeClasses();
    private static final List<Method> methods = initializeMethods();

    private static final Logger logger = LoggerFactory.getLogger(ParserUtil.class);

    private static List<Package> initializePackages(boolean isJava) {
        String targetMethodPackage = Properties.TARGET_CLASS.substring(0, Properties.TARGET_CLASS.indexOf("."));
        List<String> involvedPackages = isJava
                ? Arrays.asList("java")
                : Arrays.asList(targetMethodPackage);
        return getAllPackages().stream()
                .filter(p -> involvedPackages.stream().anyMatch(p.getName()::startsWith))
                .collect(Collectors.toList());
    }

    private static List<Class<?>> initializeClasses() {
        return packages.stream()
                .map(p -> getAllClasses(p.getName()))
                .flatMap(Set::stream)
                .collect(Collectors.toList());
    }

    private static List<Method> initializeMethods() {
        return classes.stream()
                .map(TestClusterUtils::getMethods)
                .flatMap(Set::stream)
                .collect(Collectors.toList());
    }

    private static List<Package> getAllPackages() {
        return Arrays.asList(Package.getPackages());
    }

    private static Set<Class<?>> getAllClasses(String packageName) {
        try {
            return ClassPath.from(ClassLoader.getSystemClassLoader())
                    .getAllClasses()
                    .stream()
                    .filter(clazz -> clazz.getPackageName()
                            .equalsIgnoreCase(packageName))
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new HashSet<>();
        }
    }

    private static Method getMethod(Class<?> clazz, String methodSimpleName) {
        for (Method method : Reflection.getMethods(clazz)) {
            if (method.getName().equals(methodSimpleName))
                return method;
        }
        return null;
    }

    public static List<Class<?>> loadClasses(String className) {
        return classes.stream()
                .filter(c -> c.getSimpleName().equals(className))
                .collect(Collectors.toList());
    }

    public static Class<?> loadClass(String className, String methodSimpleName) {
        Class<?> clazz = classes.stream()
                .filter(c -> c.getSimpleName().equals(className))
                .findAny().orElse(null);
        if (clazz == null) {
            for (Package p : javaPackages) {
                String packageName = p.getName();
                Set<Class<?>> classes = getAllClasses(packageName).stream()
                        .filter(c -> c.getSimpleName().equals(className))
                        .collect(Collectors.toSet());
                clazz = classes.stream()
                        .filter(c -> getMethod(c, methodSimpleName) != null)
                        .findAny().orElse(null);
                if (clazz != null) {
                    return clazz;
                }
            }
        }
        return clazz;
    }

    public static Class<?> loadClass(String methodSimpleName) {
        Class<?> enclosingClass = null;
        for (Class<?> clazz : classes) {
            enclosingClass = classes.stream()
                    .filter(c -> c.getName().equals(clazz.getName()))
                    .findAny().orElse(null);
        }
        if (enclosingClass == null) {
            for (Package p : javaPackages) {
                String packageName = p.getName();
                Set<Class<?>> classes = getAllClasses(packageName);
                enclosingClass = classes.stream()
                        .filter(c -> getMethod(c, methodSimpleName) != null)
                        .findAny().orElse(null);
                if (enclosingClass != null) {
                    return enclosingClass;
                }
            }
        }
        return enclosingClass;
    }

    public static List<Method> getMethods() {
        return methods;
    }

    public static Class<?> loadClassByName(String className) {
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
