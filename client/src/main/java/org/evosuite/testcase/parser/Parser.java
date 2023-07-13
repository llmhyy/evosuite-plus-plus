package org.evosuite.testcase.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.evosuite.testcase.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private String source;
    private CompilationUnit compilation;
    private final ParserVisitor visitor;

    private static final String CLASS_DEC_ERROR = "Parse error. Found \"void\", expected one of  \";\" \"@\" \"class\" \"enum\" \"interface\" \"module\" \"open\" \"record\"";

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public Parser() {
        this.source = null;
        this.compilation = null;
        this.visitor = new ParserVisitor();
    }

    public String transformTestSuite(String testSuiteStr) {
        StringBuilder resultTestSuite = new StringBuilder();

        ParseResult<CompilationUnit> result = new JavaParser().parse(testSuiteStr);
        boolean isSuccessful = result.isSuccessful() && result.getResult().isPresent();
        if (!isSuccessful) {
            return null;
        }

        CompilationUnit compUnit = result.getResult().get();
        Node rootNode = compUnit.getTypes().get(0);

        ClassOrInterfaceDeclaration testSuite = (ClassOrInterfaceDeclaration) rootNode;
        String testSuiteDec = testSuite.toString();
        resultTestSuite.append(testSuiteDec, 0, testSuiteDec.indexOf("\n")+1).append("\n");

        StringBuilder attributes = new StringBuilder();
        StringBuilder before = new StringBuilder();
        StringBuilder after = new StringBuilder();
        List<String> tests = new ArrayList<>();
        List<String> testDeclarations = new ArrayList<>();

        for (Node node : testSuite.getMembers()) {
            if (node instanceof FieldDeclaration) {
                String fieldStr = node.toString();
                FieldDeclaration field = (FieldDeclaration) node;
                for (Modifier modifier : field.getModifiers()) {
                    String modStr = modifier.toString();
                    fieldStr = fieldStr.replace(modStr, "");
                }
                attributes.append(fieldStr).append("\n");
            } else if (node instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) node;
                BlockStmt body = method.getBody().get();
                String annotation = method.getAnnotation(0).getNameAsString();

                if (annotation.equals("Before")) {
                    for (Statement statement : body.getStatements()) {
                        before.append("\t\t").append(statement).append("\n");
                    }
                } else if (annotation.equals("After")) {
                    for (Statement statement : body.getStatements()) {
                        after.append("\t\t").append(statement).append("\n");
                    }
                } else if (annotation.equals("Test")) {
                    StringBuilder test = new StringBuilder();
                    for (Statement statement : body.getStatements()) {
                        test.append("\t\t").append(statement).append("\n");
                    }
                    testDeclarations.add("\t@Test\n\t" + method.getDeclarationAsString() + " {\n");
                    tests.add(test.toString());
                }
            }
        }

        for (int i = 0; i < testDeclarations.size() && i < tests.size(); ++i) {
            String testDec = testDeclarations.get(i);
            String testBody = tests.get(i);

            resultTestSuite.append(testDec);
            resultTestSuite.append(attributes);
            resultTestSuite.append(before);
            resultTestSuite.append(testBody);
            resultTestSuite.append(after);
            resultTestSuite.append("}");
            resultTestSuite.append("\n\n");
        }

        resultTestSuite.append("}\n");
        return resultTestSuite.toString();
    }

    public void parse(String testCaseStr) {
        testCaseStr = removeAssertions(testCaseStr);

        ParseResult<CompilationUnit> result = new JavaParser().parse(testCaseStr);
        if (result.isSuccessful() && result.getResult().isPresent()) {
            CompilationUnit compUnit = result.getResult().get();
            Node rootNode = compUnit.getTypes().get(0);
            rootNode.accept(visitor, null);
        } else {
            List<String> errors = new ArrayList<>();
            for (Problem problem : result.getProblems()) {
                errors.add(problem.getMessage());
            }
            if (errors.contains(CLASS_DEC_ERROR)) {
                testCaseStr = wrapClassDec(testCaseStr);
                parse(testCaseStr);
            }
            logger.error(result.getProblems().toString());
        }
    }

    private String removeAssertions(String testSuiteStr) {
        StringBuilder result = new StringBuilder();
        for (String line : testSuiteStr.split(System.lineSeparator())) {
            if (!line.contains("assert")) {
                result.append(line).append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    /**
    private String handleSetUpAndTearDown(String testSuiteStr) {
        // assuming that the testSuiteStr is in this format
        // public class ... {
        // ...
        // @Before ...
        // @Test ...
        // @After ...

        String className = StringUtil.getClassSimpleName(Properties.TARGET_CLASS);
        String[] lines = testSuiteStr.split("\\\\r?\\\\n");

        StringBuiler resultBuilder = new StringBuilder();
        StringBuilder attrBuilder = new StringBuilder();
        StringBuilder setUpBuilder = new StringBuilder();
        StringBuilder tearDownBuilder = new StringBuilder();
        StringBuilder testCaseBuilder = new StringBuilder();
        boolean isAttr = false, isSetUp = false, isTearDown = false, isTestCase = false;

        for (int i = 0; i < lines.length; ++i) {
            if (!isAttr && lines[i].contains("public class ")) {
                isAttr = true;
                continue;
            } else if (!isSetUp && lines[i++].contains("@Before")) {
                isSetUp = true;
                isAttr = false;
                continue;
            } else if (!isTearDown && lines[i++].contains("@After")) {
                isTearDown = true;
                isTestCase = false;
                continue;
            } else if (!isTestCase && lines[i++].contains("@Test")) {
                isTestCase = true;
                isSetUp = false;
            }

            if (isAttr) {
                attrBuilder.append(lines[i]).append("\n");
            } else if (isSetUp) {
                setUpBuilder.append(lines[i]).append("\n");
            } else if (isTearDown) {
                tearDownBuilder.append(lines[i]).append("\n");
            }
        }
    }*/

    private String wrapClassDec(String test) {
        String startDec = "public class SampleClass {\n";
        String endDec = "\n}";
        return startDec + test + endDec;
    }

    public List<TestCase> getTestCases() {
        return this.visitor.getTestCases();
    }
}
