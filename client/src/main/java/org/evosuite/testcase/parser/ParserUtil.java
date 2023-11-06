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
import java.io.File;
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

    public static final String TARGET_PROJECT_CP = getTargetProjectPath();
    private static final Set<Class<?>> classSet = new HashSet<>();
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
                "java.util.Vector",
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
                "java.util.concurrent.CountDownLatch",
                "javax.swing.ListSelectionModel",
                "javax.swing.JList");
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

        // classSet.addAll(getAllClasses());
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

    private static String findFullyQualifiedName(String classSimpleName) {
        if (classSimpleName.equals("FieldVisitor")) {
            System.currentTimeMillis();
        }
        List<String> jarFilePaths = Arrays.asList(Properties.CP.split(File.pathSeparator));
        String targetProjectJarFilePath = jarFilePaths.stream()
                .filter(path -> !path.contains("/lib/")).findFirst().get();
        String fullClassName = findFullyQualifiedNameFromJarFile(classSimpleName, targetProjectJarFilePath);
        if (fullClassName != null) {
            return fullClassName;
        }
        for (String jarFilePath : jarFilePaths) {
            fullClassName = findFullyQualifiedNameFromJarFile(classSimpleName, jarFilePath);
            if (fullClassName != null) {
                return fullClassName;
            }
        }

        return null;
    }

    private static String findFullyQualifiedNameFromJarFile(String classSimpleName, String jarFilePath) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    // Get the fully qualified class name
                    String className = entry.getName().replace("/", ".").replaceAll(".class$", "");
                    if (className.contains("VariableSearchPanel")) {
                        System.currentTimeMillis();
                    }
                    if (className.endsWith("." + classSimpleName)) {
                        return className;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getClass(String classSimpleName) {
        // handle generics
        if (classSimpleName.contains("<") && classSimpleName.contains(">")) {
            classSimpleName = classSimpleName.substring(0, classSimpleName.indexOf("<"));
        }

        Class<?> clazz = classCache.get(classSimpleName);
        if (clazz != null) {
            return clazz;
        }

        try {
            if (classSimpleName.equals("VariableSearchPanel")) {
                System.currentTimeMillis();
            }

            if (classSimpleName.endsWith("[]")) {
                final StringBuilder arrayTypeNameBuilder = new StringBuilder(30);

                int index = 0;
                while ((index = classSimpleName.indexOf('[', index)) != -1) {
                    arrayTypeNameBuilder.append('[');
                    index++;
                }

                // always needed for Object arrays
                arrayTypeNameBuilder.append('L');
                // get full name of element type
                int indexOfBracket = classSimpleName.indexOf("[");
                String elementTypeSimpleName = classSimpleName.substring(0, indexOfBracket);
                String elementTypeFullName = getClass(elementTypeSimpleName).getCanonicalName();
                arrayTypeNameBuilder.append(elementTypeFullName);
                // finalize object array name
                arrayTypeNameBuilder.append(';');

                // (guess) only work with pre-defined type?
                clazz = Class.forName(arrayTypeNameBuilder.toString());
            } else {
                String className = findFullyQualifiedName(classSimpleName);
                if (className != null) {
                    clazz = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(className);
                    classCache.put(classSimpleName, clazz);
                }
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

    public static Set<GenericConstructor> getGenericConstructors(Class<?> clazz) {
        return getConstructors(clazz).stream()
                .map(c -> new GenericConstructor(c, clazz))
                .collect(Collectors.toSet());
    }

    public static GenericConstructor getConstructor(GenericClass clazz) {
        List<Constructor<?>> constructors = new ArrayList<>(getConstructors(clazz.getRawClass()));
        return constructors.isEmpty() ? null : new GenericConstructor(constructors.get(1), clazz);
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
                if (i == argTypes.length || (paraTypes.length == 0 && argTypes.length != 0)) {
                    // not a match if there are more parameters
                    isMatched = false;
                    break;
                } else if (!paraTypes[i].isAssignableFrom(argTypes[i]) &&
                        !argTypes[i].getCanonicalName().equals("java.lang.Object") &&
                        !(paraTypes[i].equals(long.class) && argTypes[i].equals(int.class))) { // TODO: fix
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
            if (paraTypes.length == 0 && argTypes.length > 0) {
                isMatched = false;
            }

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
        if (simpleName.equals("setDoubleClickCallback")) {
            System.currentTimeMillis();
        }

        Class<?>[] argTypes = argumentTypes.stream()
                .map(type -> type.isPrimitive() ? convertToWrapperClass(type.getRawClass()) : type.getRawClass())
                .toArray(Class[]::new);
        Set<Method> methods = new HashSet<>();
        try {
            methods = getMethods(clazz.getRawClass(), simpleName);
        } catch (Exception e) {
            System.currentTimeMillis();
        }
        for (Method method : methods) {
            Class<?>[] paraTypes = Arrays.stream(method.getParameterTypes())
                    .map(type -> type.isPrimitive() ? convertToWrapperClass(type) : type)
                    .toArray(Class[]::new);
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

    public static Class<?> convertToWrapperClass(Class<?> clazz) {
        if (clazz == int.class) {
            return Integer.class;
        } else if (clazz == long.class) {
            return Long.class;
        } else if (clazz == float.class) {
            return Float.class;
        } else if (clazz == double.class) {
            return Double.class;
        } else if (clazz == boolean.class) {
            return Boolean.class;
        } else if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == short.class) {
            return Short.class;
        } else if (clazz == char.class) {
            return Character.class;
        } else {
            return clazz;
            // throw new IllegalArgumentException("Unsupported primitive class: " + clazz);
        }
    }

    public static String getClassDefinition(String pathNames, String className) {
        logger.info("loading class definition for prompting for " + className);
        for (String pathName : pathNames.split(File.pathSeparator)) {
            String classPath = pathName.replace(".jar", "/") + className.replace(".", "/") + ".java";
            try {
                List<String> lines = Files.readAllLines(Paths.get(classPath));
                logger.info("Found class definition at " + classPath);
                return String.join("\n", lines);
            } catch (IOException e) {
                // logger.warn(e.getMessage());
            }
        }
        return "";
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
        String para = methodSignature.substring(methodSignature.indexOf('(')+1, methodSignature.indexOf(')'));
        List<String> paraList = Arrays.stream(para.split(";"))
                .filter(p -> !p.isEmpty())
                .collect(Collectors.toList());
        List<String> paraTypes = getMethodSimplePara(paraList);
        return new Pair<>(name, paraTypes.toArray(new String[0]));
    }

    private static List<String> getMethodSimplePara(List<String> paraList) {
        List<String> paraTypes = new ArrayList<>();
        for (String p : paraList) {
            for (int i = 0; i < p.length(); i++) {
                char c = p.charAt(i);
                if ("BCDFIJSZ".contains(Character.toString(c))) {
                    paraTypes.add(convertBytecodeToType(c));
                } else {
                    String s = p.substring(i);
                    s = s.substring(s.lastIndexOf('/')+1);
                    paraTypes.add(s);
                    break;
                }
            }
        }
        return paraTypes;
    }

    private static String convertBytecodeToType(char bytecode) {
        switch (bytecode) {
        case 'B': return "byte";
        case 'C': return "char";
        case 'D': return "double";
        case 'F': return "float";
        case 'I': return "int";
        case 'J': return "long";
        case 'S': return "short";
        case 'Z': return "boolean";
        default: return Character.toString(bytecode);
        }
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

    public static void getProjectClass(String pathNames, String className) {
//        logger.info("loading class definition for prompting for " + className);
//        for (String pathName : pathNames.split(":")) {
////            String classPath =
////                    pathName.replace(".jar", "/") +
////                            className.replace(".", "/") + ".java";
//            try {
//                List<String> lines = Files.readAllLines(Paths.get(classPath));
//                return String.join("\n", lines);
//            } catch (IOException e) {
//                logger.error(e.getMessage());
//            }
//        }
//        return "";
    }

    private static String getTargetProjectPath() {
        String projectPath = null;
        String[] classPaths = Properties.CP.split(File.pathSeparator);
        String targetClass = Properties.TARGET_CLASS;
        for (String path : classPaths) {
            projectPath = path.substring(0, Math.max(0, path.indexOf(".jar")));
            String classPath = projectPath + "/" + targetClass.replace(".", "/") + ".java";
            try {
                Files.readAllLines(Paths.get(classPath));
                return projectPath;
            } catch (IOException e) {
                // e.printStackTrace();
                // logger.error(e.getMessage());
            }
        }
        // should not be null as TARGET_PROJECT should exist in CP list
        assert projectPath != null;
        return projectPath;
    }
}
