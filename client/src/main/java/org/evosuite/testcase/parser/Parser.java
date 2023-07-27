package org.evosuite.testcase.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import javafx.util.Pair;
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
        private String method;
        private String declaration;
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

        public List<String> getImports() {
            return this.imports;
        }
    }

    // TODO: refactor the constructors
    public Parser(String source) {
        this.source = source;
        this.visitor = new ParserVisitor();

        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(removeAssertions(source));
        boolean isSuccessful = result.isSuccessful() && result.getResult().isPresent();
        if (isSuccessful) {
            this.compilation = result.getResult().get();
        } else {
            logger.error("error when compiling test string");
            logger.error(result.getProblems().toString());
            logger.error(source);
            logger.error("regenerating tests...");
            handleOverallException();
        }

        this.summary = new ParseResult();
        this.summary.declaration = getDeclaration();
        this.summary.fields = getFields();
        this.summary.constructors = getConstructors();
        this.summary.imports = getImports();
    }

    public Parser(String source, ParseResult summary) {
        this.source = source;
        this.summary = summary;
        this.visitor = new ParserVisitor();

        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(removeAssertions(source));
        boolean isSuccessful = result.isSuccessful() && result.getResult().isPresent();
        if (isSuccessful) {
            this.compilation = result.getResult().get();
        } else {
            logger.error("error when compiling test string");
            logger.error(result.getProblems().toString());
            logger.error(source);
            logger.error("regenerating tests...");
            handleOverallException();
        }
    }

    public Parser(String source, String targetMethodName, String... targetMethodParaTypes) {
        this.source = source;
        this.visitor = new ParserVisitor();

        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(removeAssertions(source));
        boolean isSuccessful = result.isSuccessful() && result.getResult().isPresent();
        if (isSuccessful) {
            this.compilation = result.getResult().get();
        } else {
            logger.error("error when compiling test string");
            logger.error(result.getProblems().toString());
            logger.error(source);
            logger.error("regenerating tests...");
            handleOverallException();
        }

        this.summary = new ParseResult();
        this.summary.declaration = getDeclaration();
        this.summary.method = getMethodBySignature(targetMethodName, targetMethodParaTypes);
        this.summary.fields = getFields();
        this.summary.constructors = getConstructors();
        this.summary.imports = getImports();
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
        if (isSuccessful) { // should not fail since the test suite is already parsed in the constructor
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
                break;
            } catch (ParseException e) {
                logger.error(e.getMessage());
                String[] message = e.getMessage().split(": ");
                assert message.length == 2;
                switch (message[0]) {
                    case CLASS_NOT_FOUND: handleClassNotFound(message[1]); break;
                    case CONSTRUCTOR_NOT_FOUND: handleConstructorNotFound(message[1]); break;
                    case METHOD_NOT_FOUND: handleMethodNotFound(message[1]); break;
                    default: handleOverallException(); break;
                }
            }
        } while (maxTries > 0);
    }

    private void handleClassNotFound(String classSimpleName) {
        String classFullName = ParserUtil.getClassNameFromList(summary.imports, classSimpleName);
        String className = classFullName == null ? classSimpleName : classFullName;
        source = new OpenAiLanguageModel().fixClassNotFound(source, className);
    }

    private void handleConstructorNotFound(String className) {
        String classDefinition = ParserUtil.getClassDefinition(Properties.CP, className);
        source = new OpenAiLanguageModel().fixConstructorNotFound(source, className, classDefinition);
    }

    private void handleMethodNotFound(String methodSignature) {
        String[] signature = methodSignature.split("#");
        assert signature.length == 2;
        String methodName = signature[1];
        String className = signature[0];
        String classDefinition = ParserUtil.getClassDefinition(org.evosuite.Properties.CP, className);
        source = new OpenAiLanguageModel().fixMethodNotFound(source, className, methodName, classDefinition);
    }

    private void handleOverallException() {
        String targetMethodStr = ParserUtil.getMethodSimpleSignatureStr(Properties.TARGET_METHOD);
        String targetSummaryStr = summary.toString();
        source = new OpenAiLanguageModel().getInitialPopulation(targetMethodStr, targetSummaryStr);
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

    public String getMethodBySignature(String name, String... paraTypes) {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return null;
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        List<MethodDeclaration> methods = classDec.getMethodsBySignature(name, paraTypes);
        assert methods.size() == 1;
        return methods.get(0).toString();
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

    public Map<Integer, String> getLineBranchMap(String name, String... paraTypes) {
        Map<Integer, String> lineBranchMap = new LinkedHashMap<>();

        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return null;
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        List<MethodDeclaration> methods = classDec.getMethodsBySignature(name, paraTypes);
        assert methods.size() == 1;

        List<Node> all = new ArrayList<>();
        List<Node> nodes = methods.get(0).getChildNodes();
        while (!nodes.isEmpty()) {
            all.addAll(nodes);
            nodes = nodes.stream()
                    .map(Node::getChildNodes)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        for (Node node : all) {
            Optional<Pair<Integer, String>> pair = getLineBranchPair(node);
            pair.ifPresent(p -> lineBranchMap.put(p.getKey(), p.getValue()));
        }

        return lineBranchMap;
    }

    private Optional<Pair<Integer, String>> getLineBranchPair(Node node) {
        Position position = node.getBegin().orElse(null);
        if (position == null) {
            return Optional.empty();
        }

        Expression expression = null;

        if (node instanceof IfStmt) {
            expression = ((IfStmt) node).getCondition();
        } else if (node instanceof SwitchStmt) {
            expression = ((SwitchStmt) node).getSelector();
        }

        else if (node instanceof ForStmt) {
            expression = ((ForStmt) node).getCompare().orElse(null);
        } else if (node instanceof ForEachStmt) {
            expression = ((ForEachStmt) node).getIterable();
        } else if (node instanceof WhileStmt) {
            expression = ((WhileStmt) node).getCondition();
        } else if (node instanceof DoStmt) {
            expression = ((DoStmt) node).getCondition();
        }

        if (expression == null) {
            return Optional.empty();
        }

        return Optional.of(new Pair<>(position.line, expression.toString()));
    }

    private String removeAssertions(String string) {
        return Arrays.stream(string.split("\\r?\\n"))
                .filter(s -> !s.trim().startsWith("assert"))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String getClassFromContent(String className) {
        Node root = compilation.getTypes().get(0);
        if (!(root instanceof ClassOrInterfaceDeclaration)) {
            return null;
        }

        ClassOrInterfaceDeclaration classDec = (ClassOrInterfaceDeclaration) root;
        return classDec.getChildNodes().stream()
                .filter(n -> n instanceof ClassOrInterfaceDeclaration)
                .map(n -> (ClassOrInterfaceDeclaration) n)
                .filter(n -> n.getNameAsString().equals(className))
                .map(Node::toString)
                .findAny().orElse("");
    }

    public List<TestCase> getTestCases() {
        return this.visitor.getTestCases();
    }

    public String getSource() {
        return this.source;
    }

    public ParseResult getSummary() {
        return this.summary;
    }
}
