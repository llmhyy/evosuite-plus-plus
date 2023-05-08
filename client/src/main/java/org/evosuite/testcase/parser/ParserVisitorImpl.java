package org.evosuite.testcase.parser;

import org.evosuite.Properties;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.parser.node.expr.*;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.StringPrimitiveStatement;
import org.evosuite.testcase.statements.numeric.IntPrimitiveStatement;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParserVisitorImpl implements ParserVisitor {

    private final TestCase testCase;
    private final Map<String, VariableReference> references;
    private final Map<VariableReference, Class<?>> types;
    private Statement statement;
    private String name;
    private VariableReference location;

    private static final Logger logger = LoggerFactory.getLogger(ParserVisitorImpl.class);

    public ParserVisitorImpl(TestCase testCase) {
        this.testCase = testCase;
        this.references = new HashMap<>();
        this.types = new HashMap<>();
    }

    public void visit(NameExprNode node) {
        name = node.getName();
        statement = new StringPrimitiveStatement(testCase, name);
        location = testCase.addStatement(statement);
    }

    public void visit(IntegerLiteralExprNode node) {
        Integer value = Integer.valueOf(node.getValue());
        statement = new IntPrimitiveStatement(testCase, value);
        location = testCase.addStatement(statement);
        name = location.getName();
        references.put(name, location);
        types.put(location, ParserUtil.loadClass("java.lang.Integer"));
    }

    public void visit(StringLiteralExprNode node) {
        String value = node.getValue();
        statement = new StringPrimitiveStatement(testCase, value);
        location = testCase.addStatement(statement);
        name = location.getName();
        references.put(name, location);
        types.put(location, ParserUtil.loadClass("java.lang.String"));
    }

    @Override
    public void visit(VariableDeclarationExprNode node) {
        node.getName().accept(this);
        String var = name;
        node.getInitializer().accept(this);
        references.put(var, location);
    }

    public void visit(ObjectCreationExprNode node) {
        try {
            // process class
            node.getType().accept(this);
            Class<?> clazz = ParserUtil.loadClass(name);

            // process argument list
            List<VariableReference> parameters = new ArrayList<>();
            List<Class<?>> parameterTypes = new ArrayList<>();
            node.getArguments().forEach((argument) -> {
                argument.accept(this);
                parameters.add(location);
                parameterTypes.add(types.get(location));
            });

            GenericConstructor constructor = new GenericConstructor(
                    clazz.getConstructor(parameterTypes.toArray(new Class<?>[0])),
                    clazz);
            statement = new ConstructorStatement(testCase, constructor, parameters);
            location = testCase.addStatement(statement);
            types.put(location, clazz);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
        }
    }

    public void visit(MethodCallExprNode node) {
        try {
            // process callee
            node.getCallee().accept(this);
            Class<?> clazz;
            if (references.containsKey(name)) {
                clazz = ParserUtil.loadClass("ArrayList");
            } else {
                clazz = ParserUtil.loadClass(name);
            }
            VariableReference callee = references.get(name);

            // process method name
            node.getName().accept(this);
            String name = this.name;

            // process argument list
            List<VariableReference> parameters = new ArrayList<>();
            List<Class<?>> parameterTypes = new ArrayList<>();
            node.getArguments().forEach((argument) -> {
                argument.accept(this);
                parameters.add(location);
                parameterTypes.add(types.get(location));
            });

            if (clazz == null) {
                throw new NoSuchMethodException("clazz is null");
            }
            if (Objects.equals(clazz.getName(), "java.util.ArrayList")) {
                parameterTypes.clear();
                parameterTypes.add(ParserUtil.loadClass("java.lang.Object"));
            } else if (Objects.equals(clazz.getName(), "framework.util.ObjectUtils")) {
                parameterTypes.clear();
                parameterTypes.add(ParserUtil.loadClass("java.lang.Object"));
            }
            GenericMethod method = new GenericMethod(
                    clazz.getMethod(name, parameterTypes.toArray(new Class<?>[0])),
                    clazz);
            statement = new MethodStatement(testCase, method, callee, parameters);
            location = testCase.addStatement(statement);
            types.put(location, clazz);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
        }
    }

    public TestCase getTestCase() {
        return this.testCase;
    }
}
