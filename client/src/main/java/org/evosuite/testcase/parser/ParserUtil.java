package org.evosuite.testcase.parser;

import com.google.common.reflect.ClassPath;
import org.evosuite.TestGenerationContext;
import org.evosuite.lm.OpenAiLanguageModel;
import org.evosuite.runtime.Reflection;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ParserUtil {

    private static final Map<String, GenericClass> classCache = initClass();
    private static final Map<String, GenericMethod> methodCache = initMethod();

    private static final Logger logger = LoggerFactory.getLogger(ParserUtil.class);

    private static Map<String, GenericClass> initClass() {
        Map<String, GenericClass> cache = new HashMap<>();
        List<String> fullClassNames = Arrays.asList(
                "java.lang.String",
                "java.lang.Integer",
                "java.lang.Double",
                "java.lang.Boolean",
                "java.util.List",
                "java.util.Map",
                "java.util.Set",
                "java.util.ArrayList",
                "java.util.HashMap",
                "java.util.HashSet",
                "java.util.LinkedList",
                "java.util.Queue",
                "java.util.Stack",
                "java.util.Date",
                "java.util.Calendar",
                "java.text.SimpleDateFormat",
                "java.io.File",
                "java.util.Scanner",
                "java.util.Random",
                "java.lang.System",
                "java.lang.Math",
                "java.lang.Object",
                "java.util.Arrays",
                "java.util.Collections",
                "java.util.Comparator",
                "java.util.regex.Pattern",
                "java.util.regex.Matcher",
                "java.nio.file.Path",
                "java.nio.file.Files",
                "java.net.URL",
                "java.net.HttpURLConnection",
                "java.io.InputStream",
                "java.io.OutputStream",
                "java.io.BufferedReader",
                "java.io.BufferedWriter",
                "java.io.InputStreamReader",
                "java.io.OutputStreamWriter",
                "java.util.concurrent.ExecutorService",
                "java.util.concurrent.Executors",
                "java.util.concurrent.Future",
                "java.util.concurrent.TimeUnit",
                "java.util.concurrent.locks.Lock",
                "java.util.concurrent.locks.ReentrantLock",
                "java.util.concurrent.atomic.AtomicInteger",
                "java.util.concurrent.atomic.AtomicBoolean",
                "java.util.concurrent.atomic.AtomicReference",
                "java.util.concurrent.ConcurrentHashMap",
                "java.util.concurrent.CopyOnWriteArrayList",
                "java.util.concurrent.Semaphore",
                "java.util.concurrent.CountDownLatch");
        for (String fullName : fullClassNames) {
            String simpleName = fullName.substring(fullName.lastIndexOf('.') + 1);
            try {
                GenericClass clazz = new GenericClass(Class.forName(fullName));
                cache.put(simpleName, clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return cache;
    }

    private static Map<String, GenericMethod> initMethod() {
        Map<String, GenericMethod> cache = new HashMap<>();
        GenericClass clazz;
        GenericMethod method;

        clazz = classCache.get("ArrayList");
        method = new GenericMethod(getMethod(clazz.getRawClass(), "add"), clazz);
        cache.put(clazz.getRawClass().getName() + ".add", method);

        clazz = classCache.get("Arrays");
        method = new GenericMethod(getMethod(clazz.getRawClass(), "asList"), clazz);
        cache.put(clazz.getRawClass().getName() + ".asList", method);

        return cache;
    }

    public static GenericMethod loadGenericMethod(String simpleCalleeClassName,
                                                  String simpleMethodName,
                                                  List<GenericClass> argTypes) {
        GenericClass calleeClass = classCache.get(simpleCalleeClassName);
        return loadGenericMethod(calleeClass, simpleMethodName, argTypes);
    }

    public static GenericMethod loadGenericMethod(GenericClass calleeClass,
                                                  String simpleMethodName,
                                                  List<GenericClass> argTypes) {
        // note: this does not work very well with project-declared classes
        // potentially due to the lack of information when prompting

        // TODO: find effective prompting for argTypes to address overloading

        // caching
        String simpleClassName = calleeClass.getSimpleName();

        OpenAiLanguageModel model = new OpenAiLanguageModel();
        String signature = model.findMethodSignature(simpleClassName, simpleMethodName).trim();
        String[] output = signature.split(" ");

        // variations:
        // java/util/ArrayList.add(Ljava/lang/Object;)Z
        // java.util.ArrayList.add:(Ljava/lang/Object;)Z
        // Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

        for (String s : output) {
            if (s.contains(simpleClassName) && s.contains(simpleClassName)) {
                s = s.replaceAll("\"", "");
                s = s.replaceAll("Ljava", "java");
                signature = s;
                break;
            }
        }

        int startClass = signature.indexOf(simpleClassName);
        String fullClass = signature.substring(0, startClass) + simpleClassName;
        Class<?> clazz = calleeClass.getRawClass();

        int startPara = signature.indexOf('(');
        int endPara = signature.indexOf(')');
        String[] fullParas = signature.substring(startPara + 1, endPara - 1).split(";");
        List<Class<?>> paraTypes = new ArrayList<>();
        for (String para : fullParas) {
            try {
                Class<?> type = Class.forName(para);
                paraTypes.add(type);
            } catch (ClassNotFoundException e) {
                logger.error(para + " not found");
            }
        }

        GenericMethod method = null;
        try {
            method = new GenericMethod(
                    clazz.getMethod(simpleMethodName, paraTypes.toArray(new Class<?>[0])),
                    clazz);
        } catch (NoSuchMethodException e) {
            logger.error("cannot find method matched signature " + signature);
        }

        return method;
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
}
