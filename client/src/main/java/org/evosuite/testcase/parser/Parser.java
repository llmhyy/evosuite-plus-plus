package org.evosuite.testcase.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.evosuite.Properties;
import org.evosuite.lm.OpenAiLanguageModel;
import org.evosuite.testcase.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.evosuite.testcase.parser.ParseException.*;

public class Parser {

    private String source;
    private ParseResult summary;
    private CompilationUnit compilation;
    private final ParserVisitor visitor;

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    private static final String NEWLINE = System.lineSeparator();

    public static class ParseResult {
        private String declaration;
        private String method;
        private List<String> fields = new ArrayList<>();
        private List<String> constructors = new ArrayList<>();
        private List<String> imports = new ArrayList<>();

        @Override
        public String toString() {
            return declaration + NEWLINE + NEWLINE +
                    method + NEWLINE + NEWLINE +
                    String.join(NEWLINE, fields) + NEWLINE + NEWLINE +
                    String.join(NEWLINE, constructors) + NEWLINE + NEWLINE +
                    "}" + NEWLINE;
        }
    }

    public Parser(String source) {
        this.source = source;
        this.visitor = new ParserVisitor();

        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(source);
        boolean isSuccessful = result.isSuccessful() && result.getResult().isPresent();
        if (isSuccessful) {
            this.compilation = result.getResult().get();
        }
    }

    public Parser(String source, String targetMethodName, String... targetMethodParaTypes) {
        this(source);
        this.summary = new ParseResult();
        this.summary.declaration = getDeclaration();
        this.summary.method = getMethodBySignature(targetMethodName, targetMethodParaTypes).get(0);
        this.summary.fields = getFields();
        this.summary.constructors = getConstructors();
        this.summary.imports = getImports();
    }

    public String getDeclaration() {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return "";
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        return classDec.toString().split("\\r?\\n")[0];
    }

    public List<String> getFields() {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return new ArrayList<>();
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        return classDec.getFields()
                .stream()
                .map(FieldDeclaration::toString)
                .collect(Collectors.toList());
    }

    public List<String> getConstructors() {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return new ArrayList<>();
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        return classDec.getConstructors()
                .stream()
                .map(ConstructorDeclaration::toString)
                .collect(Collectors.toList());
    }

    public List<String> getMethodBySignature(String name, String... paraTypes) {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return new ArrayList<>();
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        return classDec.getMethodsBySignature(name, paraTypes)
                .stream()
                .map(MethodDeclaration::toString)
                .collect(Collectors.toList());
    }

    public List<MethodDeclaration> getMethodsByAnnotation(String annotation) {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return new ArrayList<>();
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        return classDec.getMethods()
                .stream()
                .filter(m -> m.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public List<String> getImports() {
        List<String> filter = Arrays.asList(
                "java.lang.",
                "java.util.",
                "java.awt.");
        return compilation.getImports()
                .stream()
                .map(ImportDeclaration::getNameAsString)
                .filter(i -> filter.stream().noneMatch(i::startsWith))
                .collect(Collectors.toList());
    }

    public void handleSetUpAndTearDown() {
        StringBuilder testSuiteBuilder = new StringBuilder();

        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return;
        }

        String declaration = getDeclaration();
        List<String> fields = getFields();
        List<MethodDeclaration> tests = getMethodsByAnnotation("Test");
        List<MethodDeclaration> before = getMethodsByAnnotation("Before");
        List<MethodDeclaration> after = getMethodsByAnnotation("After");

        List<String> beforeStatements = ParserUtil.getMethodStatements(before);
        List<String> afterStatements = ParserUtil.getMethodStatements(after);

        String fieldsStr = String.join(NEWLINE, fields);
        String beforeStr = String.join(NEWLINE, beforeStatements);
        String afterStr = String.join(NEWLINE, afterStatements);

        testSuiteBuilder.append(declaration).append(NEWLINE).append(NEWLINE);

        for (MethodDeclaration test : tests) {
            String testDeclaration = test.getDeclarationAsString(true, true);
            List<String> testStatements = ParserUtil.getMethodStatements(Collections.singletonList(test));
            String testStr = String.join(NEWLINE, testStatements);

            testSuiteBuilder.append("@Test").append(NEWLINE)
                    .append(testDeclaration).append(" {").append(NEWLINE)
                    .append(fieldsStr).append(fieldsStr.isEmpty() ? "" : NEWLINE)
                    .append(beforeStr).append(beforeStr.isEmpty() ? "" : NEWLINE)
                    .append(testStr).append(testStr.isEmpty() ? "" : NEWLINE)
                    .append(afterStr).append(afterStr.isEmpty() ? "" : NEWLINE)
                    .append("}").append(NEWLINE).append(NEWLINE);
        }

        testSuiteBuilder.append("}").append(NEWLINE);
        String testSuiteStr = testSuiteBuilder.toString();

        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(testSuiteStr);
        boolean isSuccessful = result.isSuccessful() && result.getResult().isPresent();
        if (isSuccessful) {
            source = testSuiteStr;
            compilation = result.getResult().get();
        }
    }

    public void parse(int maxTries) {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return;
        }

        do {
            try {
                maxTries--;
                root.accept(visitor, null);
            } catch (ParseException e) {
                String[] message = e.getMessage().split(": ");
                assert message.length == 2;
                switch (message[0]) {
                    case CLASS_NOT_FOUND: handleClassNotFound(message[1]); break;
                    case CONSTRUCTOR_NOT_FOUND: handleConstructorNotFound(message[1]); break;
                    case METHOD_NOT_FOUND: handleMethodNotFound(message[1]); break;
                }
            }
        } while (maxTries > 0);
    }

    private void handleClassNotFound(String detail) {

    }

    private void handleConstructorNotFound(String detail) {
        String className = detail;
        String classDefinition = ParserUtil.getClassDefinition(Properties.CP, className);
        source = new OpenAiLanguageModel().fixConstructorNotFound(source, className, classDefinition);
    }

    private void handleMethodNotFound(String detail) {
        String[] method = detail.split("#");
        assert method.length == 2;
        String methodName = method[0];
        String className = method[1];
        String classDefinition = ParserUtil.getClassDefinition(org.evosuite.Properties.CP, className);
        source = new OpenAiLanguageModel().fixMethodNotFound(source, className, methodName, classDefinition);
    }

    public List<TestCase> getTestCases() {
        return this.visitor.getTestCases();
    }

    public ParseResult getSummary() {
        return this.summary;
    }
}
