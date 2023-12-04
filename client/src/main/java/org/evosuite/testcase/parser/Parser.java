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
import com.github.javaparser.ast.expr.BinaryExpr;
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
    private String originalSrc;
    private CompilationUnit compilation;
    private ParserVisitor visitor;
    private ParseResult summary;

    // TODO: refactor later
    private Map<String, String> contextMap;

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

    public Parser(String source, ParseResult summary) {
        setSource(source, true);
        setCompilation();
        setVisitor();
        setSummary(summary);
    }

    public Parser(String testSuiteStr) {
        this(testSuiteStr, null);
    }

    public Parser(String source, String targetMethodName, String... targetMethodParaTypes) {
        setSource(source, false);
        setCompilation();
        setVisitor();

        assert compilation != null && !compilation.getTypes().isEmpty();
        assert compilation.getType(0) instanceof ClassOrInterfaceDeclaration;
        ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration) compilation.getType(0);

        ParseResult summary = new ParseResult();
        summary.declaration = getDeclaration(declaration);
        summary.method = getMethodBySignature(declaration, targetMethodName, targetMethodParaTypes);
        summary.fields = getFields(declaration);
        summary.constructors = getConstructors(declaration);
        summary.imports = getImports(compilation);

        setSummary(summary);
    }

    public Parser(Map<String, String> classDefMap, Map<String, List<Pair<String, String[]>>> classMethodMap) {
        this.contextMap = new HashMap<>();

        Set<String> classSet = classDefMap.keySet();
        for (String className : classSet) {
            String classDef = classDefMap.get(className);
            List<Pair<String, String[]>> methodSignatureList = classMethodMap.get(className);

            // Sanity check
            if (classDef.isEmpty()) {
                continue;
            }

            // Get AST of class definition
            setSource(classDef, false);
            setCompilation();
            assert compilation != null && !compilation.getTypes().isEmpty();
            assert compilation.getType(0) instanceof ClassOrInterfaceDeclaration;
            ClassOrInterfaceDeclaration classDefNode = (ClassOrInterfaceDeclaration) compilation.getType(0);

            // Extract class declaration
            String classDec = getDeclaration(classDefNode);

            // Extract method declarations
            List<String> methodDefList = new ArrayList<>();
            for (Pair<String, String[]> methodSignature : methodSignatureList) {
                String methodName = methodSignature.getKey();
                String[] methodParas = methodSignature.getValue();
                String methodDef = getMethodBySignature(classDefNode, methodName, methodParas);
                methodDefList.add(methodDef);
            }

            // Build summarized class definition
            String summarizedClassDef = classDec + NEWLINE + NEWLINE +
                    String.join(NEWLINE, methodDefList) + NEWLINE + NEWLINE +
                    "}" + NEWLINE;

            // Put entry to map context
            contextMap.put(className, summarizedClassDef);
        }
    }

    public void parse(int maxTries) {
        boolean invalid = false;
        do {
            assert compilation != null && !compilation.getTypes().isEmpty();
            assert compilation.getType(0) instanceof ClassOrInterfaceDeclaration;
            ClassOrInterfaceDeclaration root = (ClassOrInterfaceDeclaration) compilation.getType(0);

            try {
                maxTries--;
                root.accept(visitor, null);
                // for prompting LLM in batch
                //checkForExceptions();
            } catch (ParseException e) {
                invalid = true;
                logger.error(e.getMessage());
                String[] message = e.getMessage().split(": ");
                assert message.length == 2;
                switch (message[0]) {
                    case CLASS_NOT_FOUND_MSG: handleClassNotFound(message[1]); break;
                    case CTOR_NOT_FOUND_MSG: handleConstructorNotFound(message[1]); break;
                    case METHOD_NOT_FOUND_MSG: handleMethodNotFound(message[1]); break;
                    default: handleOverallException(); break;
                }
            } catch (RuntimeException e) {
                logger.error(e.getMessage());
            }
        } while (maxTries > 0 && invalid);
    }

    private void checkForExceptions() {
        Set<String> ctorsDefinitions = new HashSet<>();
        Set<String> methodDefinitions = new HashSet<>();
    }

    private void handleClassNotFound(String classSimpleName) {
        String classFullName = ParserUtil.getClassNameFromList(summary.imports, classSimpleName);
        String className = classFullName == null ? classSimpleName : classFullName;
        String newTests = new OpenAiLanguageModel().fixClassNotFound(originalSrc, className);
        setSource(newTests, true);
        setCompilation();
    }

    private void handleConstructorNotFound(String className) {
        String simpleName = ParserUtil.getClassSimpleName(className);
        String classDefinition = ParserUtil.getClassDefinition(Properties.CP, className);
        String newTests = new OpenAiLanguageModel().fixConstructorNotFound(originalSrc, simpleName, classDefinition);
        setSource(newTests, true);
        setCompilation();
    }

    private void handleMethodNotFound(String methodSignature) {
        String[] signature = methodSignature.split("#");
        assert signature.length == 2;
        String methodName = signature[1];
        String className = signature[0];
        String classDefinition = ParserUtil.getClassDefinition(org.evosuite.Properties.CP, className);
        String newTests = new OpenAiLanguageModel().fixMethodNotFound(originalSrc, className, methodName, classDefinition);
        setSource(newTests, true);
        setCompilation();
    }

    private void handleOverallException() {
        String targetMethodStr = ParserUtil.getMethodSimpleSignatureStr(Properties.TARGET_METHOD);
        String targetSummaryStr = summary.toString();
        String newTests = new OpenAiLanguageModel().getInitialPopulation(targetMethodStr, targetSummaryStr);
        setSource(newTests, true);
        setCompilation();
    }

    public String getDeclaration(ClassOrInterfaceDeclaration declaration) {
        return declaration.toString().split("\\r?\\n")[0];
    }

    public List<String> getFields(ClassOrInterfaceDeclaration declaration) {
        return declaration.getFields()
                .stream()
                .map(FieldDeclaration::toString)
                .collect(Collectors.toList());
    }

    public List<String> getFieldsWoModifiers(ClassOrInterfaceDeclaration declaration) {
        List<String> modifiers = Arrays.asList("public ", "private ", "protected ");
        return declaration.getFields()
                .stream()
                .map(f -> modifiers
                        .stream()
                        .reduce(f.toString(), (field, m) -> field.replaceAll(m, "")))
                .collect(Collectors.toList());
    }

    public List<String> getConstructors(ClassOrInterfaceDeclaration declaration) {
        return declaration.getConstructors()
                .stream()
                .map(ConstructorDeclaration::toString)
                .collect(Collectors.toList());
    }

    public String getMethodBySignature(ClassOrInterfaceDeclaration declaration, String name, String... paraTypes) {
        List<MethodDeclaration> methods = declaration.getMethodsBySignature(name, paraTypes);
        if (methods.isEmpty()) {
            methods = declaration.getMethodsByName(name)
                    .stream().filter(m -> m.getParameters().size() == paraTypes.length)
                    .collect(Collectors.toList());
        }
        // Add error handling for not finding method in definition
        return methods.size() == 1 ? methods.get(0).toString() : "";
    }

    public List<MethodDeclaration> getMethodsByAnnotation(ClassOrInterfaceDeclaration declaration, String annotation) {
        return declaration.getMethods()
                .stream()
                .filter(m -> m.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public List<String> getImports(CompilationUnit unit) {
        List<String> filter = Arrays.asList(
                "java.lang.",
                "java.util.",
                "java.awt.");
        return unit.getImports()
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
        if (methods.isEmpty()) {
            methods = classDec.getMethodsByName(name)
                    .stream().filter(m -> m.getParameters().size() == paraTypes.length)
                    .collect(Collectors.toList());
        }
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

        if (node instanceof ReturnStmt) {
            System.currentTimeMillis();
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

        else if (node instanceof ReturnStmt) {
            expression = ((ReturnStmt) node).getExpression().orElse(null);
            if (!(expression instanceof BinaryExpr)) {
                expression = null;
            }

        }

        if (expression == null) {
            return Optional.empty();
        }

        return Optional.of(new Pair<>(position.line, expression.toString()));
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

    public boolean isParsable(String source) {
        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(source);
        boolean isParsable = result.isSuccessful() && result.getResult().isPresent();
        if (!isParsable) {
            logger.error("error when compiling test string");
            logger.error(result.getProblems().toString());
            logger.error(source);
        }

        return isParsable;
    }

    private String removeAssertions(String source) {
        return Arrays.stream(source.split("\\r?\\n"))
                .filter(s -> !s.trim().toLowerCase().startsWith("assert"))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String mergeSetUpAndTearDown(String source) {
        StringBuilder testSuiteBuilder = new StringBuilder();

        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(source);
        assert result.isSuccessful() && result.getResult().isPresent();
        CompilationUnit unit = result.getResult().get();
        assert unit.getType(0) instanceof ClassOrInterfaceDeclaration;
        ClassOrInterfaceDeclaration root = (ClassOrInterfaceDeclaration) unit.getType(0);

        String declaration = getDeclaration(root);
        List<String> fields = getFieldsWoModifiers(root);
        List<MethodDeclaration> tests = getMethodsByAnnotation(root, "Test");
        List<MethodDeclaration> before = getMethodsByAnnotation(root, "Before");
        List<MethodDeclaration> after = getMethodsByAnnotation(root, "After");

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
        return testSuiteBuilder.toString();
    }

    public void setSource(String source, boolean isTestSuite) {
        if (!isParsable(source)) {
            handleOverallException();
            // throw new ParseException(GENERAL_ERR);
        }

        String s = source;
        s = isTestSuite ? removeAssertions(s) : s;
        s = isTestSuite ? mergeSetUpAndTearDown(s) : s;
        this.source = s;
        this.originalSrc = source;
    }

    public void setCompilation() {
        com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(source);
        assert result.isSuccessful() && result.getResult().isPresent();
        this.compilation = result.getResult().get();
    }

    public void setVisitor() {
        this.visitor = new ParserVisitor();
    }

    public void setSummary(ParseResult summary) {
        this.summary = summary;
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

    public Map<String, String> getContextMap() {
        return this.contextMap;
    }
}
