package org.evosuite.testcase.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.evosuite.Properties;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.parser.node.AstNode;
import org.evosuite.testcase.parser.node.AstNodeTransformer;
import org.evosuite.testcase.parser.node.expr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Parser {

    private final ParserVisitorImpl visitor;

    private static final String CLASS_DEC_ERROR = "Parse error. Found \"void\", expected one of  \";\" \"@\" \"class\" \"enum\" \"interface\" \"module\" \"open\" \"record\"";

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public Parser() {
        this.visitor = new ParserVisitorImpl(new DefaultTestCase());
    }

    public void parse(String testCaseStr) {
        ParseResult<CompilationUnit> result = new JavaParser().parse(testCaseStr);
        if (result.isSuccessful() && result.getResult().isPresent()) {
            CompilationUnit compUnit = result.getResult().get();
            Node rootNode = compUnit.getTypes().get(0);
            List<ExpressionStmt> testStatements = getTestStatements(rootNode);
            List<AstNode> testNodes = testStatements.stream()
                    .map(this::transformTestStatement)
                    .collect(Collectors.toList());
            testNodes.forEach(this::parseAstNode);
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

    private String wrapClassDec(String test) {
        String startDec = "public class SampleClass {\n";
        String endDec = "\n}";
        return startDec + test + endDec;
    }

    private void parseAstNode(AstNode node) {
        if (node != null) {
            node.accept(visitor);
        }
    }

    private AstNode transformTestStatement(ExpressionStmt statement) {
        return AstNodeTransformer.transform(statement.getExpression());
    }

    private List<ExpressionStmt> getTestStatements(Node root) {
        List<ExpressionStmt> statements = new ArrayList<>();
        if (root instanceof ClassOrInterfaceDeclaration) {
            Optional<Node> methodDec = root.getChildNodes().stream()
                    .filter((child) -> child instanceof MethodDeclaration)
                    .findFirst(); // get the first test
            if (methodDec.isPresent()) {
                BlockStmt blockStmt = ((MethodDeclaration) methodDec.get()).getBody().orElse(new BlockStmt());
                statements = Arrays.stream(blockStmt.getStatements().toArray(new Statement[0]))
                        .map(Statement::asExpressionStmt)
                        .collect(Collectors.toList());
            } else {
                logger.info("no test created by LLM");
            }
        } else {
            logger.error("root node should be a ClassOrInterfaceDeclaration instance");
        }
        return statements;
    }

    public TestCase getTestCase() {
        return this.visitor.getTestCase();
    }
}
