package org.evosuite.testcase.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import org.evosuite.testcase.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final ParserVisitor visitor;

    private static final String CLASS_DEC_ERROR = "Parse error. Found \"void\", expected one of  \";\" \"@\" \"class\" \"enum\" \"interface\" \"module\" \"open\" \"record\"";

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public Parser() {
        this.visitor = new ParserVisitor();
    }

    public void parse(String testCaseStr) {
        testCaseStr = removeAssertions(testCaseStr);
        System.out.println("AFTER REMOVING ASSERTIONS");
        System.out.println(testCaseStr);
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

    private String removeAssertions(String testCaseStr) {
        StringBuilder result = new StringBuilder();
        for (String line : testCaseStr.split(System.lineSeparator())) {
            if (!line.contains("assert")) {
                result.append(line).append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    private String wrapClassDec(String test) {
        String startDec = "public class SampleClass {\n";
        String endDec = "\n}";
        return startDec + test + endDec;
    }

    public TestCase getTestCase() {
        return this.visitor.getTestCase();
    }
}
