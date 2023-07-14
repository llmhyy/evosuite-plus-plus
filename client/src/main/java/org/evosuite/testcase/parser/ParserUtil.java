package org.evosuite.testcase.parser;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.google.common.reflect.ClassPath;
import javafx.util.Pair;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.lm.OpenAiLanguageModel;
import org.evosuite.runtime.Reflection;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ParserUtil {

    private static final Logger logger = LoggerFactory.getLogger(ParserUtil.class);

    private static final Map<String, Class<?>> classCache = new HashMap<>();
    private static final Map<Class<?>, Set<Method>> methodCache = new HashMap<>();
    private static final Map<Class<?>, Set<Constructor<?>>> constructorCache = new HashMap<>();

    public static void initCaches() {
        initClass();
        initMethodCache();
        initConstructor();
    }

    private static void initClass() {
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
                "java.util.Collection",
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
                Class<?> clazz = Class.forName(fullName);
                classCache.put(simpleName, clazz);
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
            }
        }

        classCache.put("int", int.class);
        classCache.put("long", long.class);
        classCache.put("float", float.class);
        classCache.put("double", double.class);
        classCache.put("boolean", boolean.class);
        classCache.put("byte", byte.class);
        classCache.put("short", short.class);
        classCache.put("char", char.class);
    }

    private static void initMethodCache() {
        Class<?> clazz;
        Set<Method> methods;

        clazz = classCache.get("ArrayList");
        methods = getMethods(clazz);
        methodCache.put(clazz, methods);

        clazz = classCache.get("Arrays");
        methods = getMethods(clazz);
        methodCache.put(clazz, methods);
    }

    private static void initConstructor() {
        Class<?> clazz;
        Set<Constructor<?>> constructors;

        clazz = classCache.get("ArrayList");
        constructors = getConstructors(clazz);
        constructorCache.put(clazz, constructors);
    }

    public static GenericMethod loadGenericMethod(String simpleCalleeClassName,
                                                  String simpleMethodName,
                                                  List<GenericClass> argTypes) {
        Class<?> clazz = classCache.get(simpleCalleeClassName);
        GenericClass calleeClass = new GenericClass(clazz);
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

    public static Class<?> getClass(String classSimpleName) {
        Class<?> clazz = null;
        try {
            clazz = classCache.get(classSimpleName);
            if (clazz == null && classSimpleName.equals(Properties.TARGET_CLASS.substring(Properties.TARGET_CLASS.lastIndexOf('.') + 1))) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Properties.TARGET_CLASS);
            }

            else if (classSimpleName.equals("User")) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass("macaw.businessLayer.User");
            } else if (classSimpleName.equals("Variable")) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass("macaw.businessLayer.Variable");
            } else if (classSimpleName.equals("OntologyTerm")) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass("macaw.businessLayer.OntologyTerm");
            } else if (classSimpleName.equals("InMemoryChangeEventManager")) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass("macaw.persistenceLayer.demo.InMemoryChangeEventManager");
            } else if (classSimpleName.equals("InMemoryListChoiceManager")) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass("macaw.persistenceLayer.demo.InMemoryListChoiceManager");
            } else if (classSimpleName.equals("InMemoryOntologyTermManager")) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass("macaw.persistenceLayer.demo.InMemoryOntologyTermManager");
            } else if (classSimpleName.equals("InMemorySupportingDocumentsManager")) {
                clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass("macaw.persistenceLayer.demo.InMemorySupportingDocumentsManager");
            }
        } catch (ClassNotFoundException e) {
            logger.error("class " + classSimpleName + " not found");
        }
        return clazz;
    }

    public static GenericClass getGenericClass(String classSimpleName) {
        Class<?> clazz = getClass(classSimpleName);
        return clazz == null ? null : new GenericClass(clazz);
    }

    public static Set<Constructor<?>> getConstructors(Class<?> clazz) {
        Map<String, Constructor<?>> helper = new TreeMap<>();

        Set<Constructor<?>> constructors = new LinkedHashSet<>();
        for (Constructor<?> c : Reflection.getDeclaredConstructors(clazz)) {
            helper.put(org.objectweb.asm.Type.getConstructorDescriptor(c), c);
        }
        for (Constructor<?> c : helper.values()) {
            constructors.add(c);
        }
        return constructors;
    }

    public static GenericConstructor getConstructor(GenericClass clazz,
                                                    List<GenericClass> argumentTypes) {
        if (clazz.getSimpleName().equals("Variable")) {
            System.currentTimeMillis();
        }
        Class<?>[] argTypes = argumentTypes.stream()
                .map(GenericClass::getRawClass)
                .toArray(Class[]::new);
        Set<Constructor<?>> constructors = getConstructors(clazz.getRawClass());
        for (Constructor<?> constructor : constructors) {
            Class<?>[] paraTypes = constructor.getParameterTypes();
            int i = 0;
            boolean isMatched = true;
            while (i < paraTypes.length && i < argTypes.length) {
                if (!paraTypes[i].isAssignableFrom(argTypes[i])) {
                    // if the type is not assignable, check if the method has vararg and
                    // the current parameter type is the last type and is an array type
                    if (constructor.isVarArgs() && i == paraTypes.length-1 && paraTypes[i].isArray()) {
                        Class<?> varArgType = paraTypes[i].getComponentType();
                        boolean isAllAssignable = true;
                        for (int j = i; j < argTypes.length; j++) {
                            if (!varArgType.isAssignableFrom(argTypes[j])) {
                                isAllAssignable = false;
                                break;
                            }
                        }
                        // no need to break since i should be at the last element
                        isMatched = isAllAssignable;
                    } else {
                        isMatched = false;
                        break;
                    }
                }
                i++;
            }

//            // TODO: temp fix ONLY
//            if (paraTypes.length == 0 && argTypes.length > 0) {
//                isMatched = false;
//            }

            if (isMatched) {
                return new GenericConstructor(constructor, clazz);
            }
        }
        return null;
    }

    public static Set<Method> getMethods(Class<?> clazz) {
        if (methodCache.containsKey(clazz)) {
            return methodCache.get(clazz);
        }
        Map<String, Method> helper = new TreeMap<String, Method>();

        if (clazz.getSuperclass() != null) {
            for (Method m : getMethods(clazz.getSuperclass())) {
                helper.put(m.getName() + org.objectweb.asm.Type.getMethodDescriptor(m), m);
            }
        }
        for (Class<?> in : Reflection.getInterfaces(clazz)) {
            for (Method m : getMethods(in)) {
                helper.put(m.getName() + org.objectweb.asm.Type.getMethodDescriptor(m), m);
            }
        }

        for (Method m : Reflection.getDeclaredMethods(clazz)) {
            helper.put(m.getName() + org.objectweb.asm.Type.getMethodDescriptor(m), m);
        }

        Set<Method> methods = new LinkedHashSet<>();
        methods.addAll(helper.values());
        methodCache.put(clazz, methods);
        return methods;
    }

    public static Set<Method> getMethods(Class<?> clazz, String methodSimpleName) {
        Set<Method> methods = new HashSet<>();
        for (Method method : Reflection.getMethods(clazz)) {
            if (method.getName().equals(methodSimpleName))
                methods.add(method);
        }
        return methods;
    }

    public static Method getMethod(Class<?> clazz, String methodSimpleName) {
        for (Method method : Reflection.getMethods(clazz)) {
            if (method.getName().equals(methodSimpleName))
                return method;
        }
        return null;
    }

    public static GenericMethod getMethod(GenericClass clazz,
                                          String simpleName,
                                          List<GenericClass> argumentTypes) {
        Class<?>[] argTypes = argumentTypes.stream()
                .map(type -> type.isPrimitive()
                        ? convertToWrapperClass(type.getRawClass())
                        : type.getRawClass())
                .toArray(Class[]::new);
        Set<Method> methods = new HashSet<>();
        try {
            methods = getMethods(clazz.getRawClass(), simpleName);
        } catch (Exception e) {
            System.currentTimeMillis();
        }
        for (Method method : methods) {
            Class<?>[] paraTypes = method.getParameterTypes();
            int i = 0;
            boolean isMatched = true;
            while (i < paraTypes.length) {
                if (i == argTypes.length) {
                    // not a match if there are more parameters
                    isMatched = false;
                    break;
                } else if (!paraTypes[i].isAssignableFrom(argTypes[i])) {
                    // if the type is not assignable, check if the method has vararg and
                    // the current parameter type is the last type and is an array type
                    if (method.isVarArgs() && i == paraTypes.length-1 && paraTypes[i].isArray()) {
                        Class<?> varArgType = paraTypes[i].getComponentType();
                        boolean isAllAssignable = true;
                        for (int j = i; j < argTypes.length; j++) {
                            if (!varArgType.isAssignableFrom(argTypes[j])) {
                                isAllAssignable = false;
                                break;
                            }
                        }
                        // no need to break since i should be at the last element
                        isMatched = isAllAssignable;
                    } else {
                        isMatched = false;
                        break;
                    }
                }
                i++;
            }

            if (isMatched) {
                return new GenericMethod(method, clazz);
            }
        }
        return null;
    }

    public static Class<?> convertToWrapperClass(Class<?> primitiveClass) {
        if (primitiveClass == int.class) {
            return Integer.class;
        } else if (primitiveClass == long.class) {
            return Long.class;
        } else if (primitiveClass == float.class) {
            return Float.class;
        } else if (primitiveClass == double.class) {
            return Double.class;
        } else if (primitiveClass == boolean.class) {
            return Boolean.class;
        } else if (primitiveClass == byte.class) {
            return Byte.class;
        } else if (primitiveClass == short.class) {
            return Short.class;
        } else if (primitiveClass == char.class) {
            return Character.class;
        } else {
            throw new IllegalArgumentException("Unsupported primitive class: " + primitiveClass);
        }
    }

    public static String getClassDefinition(String pathNames, String className) {
        logger.info("loading class definition for prompting for " + className);
        for (String pathName : pathNames.split(":")) {
            String classPath =
                    pathName.replace(".jar", "/src/main/java/") +
                    className.replace(".", "/") + ".java";
            try {
                List<String> lines = Files.readAllLines(Paths.get(classPath));
                return String.join("\n", lines);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

            /*
            File jarFile = new File(pathName);

            try (JarFile jar = new JarFile(jarFile)) {
                JarEntry entry = jar.getJarEntry(className.replace('.', '/') + ".class");
                if (entry == null) {
                    continue;
                }

                InputStream inputStream = jar.getInputStream(entry);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // Read the class file and write it to the output stream
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Convert the byte array to a String representation
                String classDefinition = outputStream.toString("UTF-8");

                System.out.println(classDefinition);

                return classDefinition;
            } catch (IOException e) {
                logger.error(e.getMessage());
                return "";
            }
            */
        }
        return "";
    }

    public static String getClassDeclaration(String classDefinition) {
        String className = ParserUtil.getClassSimpleName(Properties.TARGET_CLASS);
        StringBuilder classDeclaration = new StringBuilder();
        String[] lines = classDefinition.split("\\r?\\n");
        for (String l : lines) {
            classDeclaration.append(l).append("\n");
            if (l.contains(className)) {
                break;
            }
        }
        return classDeclaration.toString();
    }

    public static String getClassNameFromList(List<String> classList, String classSimpleName) {
        for (String className : classList) {
            if (ParserUtil.getClassSimpleName(className).equals(classSimpleName)) {
                return className;
            }
        }
        return null;
    }

    private static byte[] readClassBytes(JarFile jarFile, JarEntry entry) {
        try {
            InputStream is = jarFile.getInputStream(entry);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new byte[]{};
        }
    }



    public static String getClassSimpleName(String className) {
        return className.substring(className.lastIndexOf('.')+1);
    }

    public static Pair<String, String[]> getMethodSimpleSignature(String methodSignature) {
        String name = methodSignature.substring(0, methodSignature.indexOf('('));
        String[] paraList = methodSignature.substring(methodSignature.indexOf('(')+1, methodSignature.indexOf(')')).split(";");
        String[] paraTypes = new String[paraList.length];
        for (int i = 0; i < paraList.length; ++i) {
            String paraType = paraList[i].substring(paraList[i].lastIndexOf('/')+1);
            if (!paraType.isEmpty()) {
                paraTypes[i] = paraType;
            }
        }
        return new Pair<>(name, paraTypes);
    }

    public static String getMethodSimpleSignatureStr(String methodSignature) {
        Pair<String, String[]> simpleSignature = getMethodSimpleSignature(methodSignature);
        String name = simpleSignature.getKey();
        String paraTypes = String.join(", ", simpleSignature.getValue());
        return name + "(" + paraTypes + ")";
    }

    public static List<String> getMethodStatements(List<MethodDeclaration> methods) {
        return methods
                .stream()
                .map(MethodDeclaration::getBody)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(BlockStmt::getStatements)
                .flatMap(NodeList::stream)
                .map(Statement::toString)
                .collect(Collectors.toList());
    }
}
