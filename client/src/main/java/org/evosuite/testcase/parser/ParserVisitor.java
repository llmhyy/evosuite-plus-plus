package org.evosuite.testcase.parser;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.*;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.numeric.*;
import org.evosuite.testcase.variable.*;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import javax.swing.JList;

import static org.evosuite.testcase.parser.ParseException.*;

public class ParserVisitor implements VoidVisitor<Object> {

    private final List<TestCase> testCases;
    private TestCase testCase;
    private Map<String, VariableReference> testRefs;

    private Statement s;
    private VariableReference r;

    private static final Logger logger = LoggerFactory.getLogger(ParserVisitor.class);

    public ParserVisitor() {
        this.testCases = new ArrayList<>();
        this.testRefs = new HashMap<>();
        this.testCase = new DefaultTestCase();
        ParserUtil.initCaches();
    }

    public List<TestCase> getTestCases() {
        return this.testCases;
    }

    private void updateReference(String k, VariableReference v) {
        this.testRefs.put(k, v);
    }

    private VariableReference getReference(String k) {
        return this.testRefs.get(k);
    }

    public TestCase getTestCase() {
        return this.testCase;
    }

    // - Compilation Unit ----------------------------------
    @Override
    public void visit(CompilationUnit n, Object arg) {

    }

    @Override
    public void visit(PackageDeclaration n, Object arg) {

    }

    @Override
    public void visit(TypeParameter n, Object arg) {

    }

    @Override
    public void visit(LineComment n, Object arg) {

    }

    @Override
    public void visit(BlockComment n, Object arg) {

    }

    // - Body ----------------------------------------------
    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        n.getMembers().accept(this, arg);
    }

    @Override
    public void visit(RecordDeclaration n, Object arg) {

    }

    @Override
    public void visit(CompactConstructorDeclaration n, Object arg) {

    }

    @Override
    public void visit(EnumDeclaration n, Object arg) {

    }

    @Override
    public void visit(EnumConstantDeclaration n, Object arg) {

    }

    @Override
    public void visit(AnnotationDeclaration n, Object arg) {

    }

    @Override
    public void visit(AnnotationMemberDeclaration n, Object arg) {

    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        Type type = n.getElementType();
        n.getVariables().accept(this, type);
    }

    @Override
    public void visit(VariableDeclarator n, Object arg) {
        Class<?> type = arg instanceof ClassOrInterfaceType ? ParserUtil.getClass(arg.toString()) : null;
        Optional<Expression> init = n.getInitializer();
        init.ifPresent(i -> i.accept(this, arg));

        s = new NullStatement(testCase, type);
        r = init.isPresent() ? r : testCase.addStatement(s);
        updateReference(n.getName().asString(), r);
    }

    @Override
    public void visit(ConstructorDeclaration n, Object arg) {

    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        String annotation = n.getAnnotation(0).getNameAsString();
        n.getBody().ifPresent(b -> b.accept(this, arg));
        testCases.add(testCase);
        testCase = new DefaultTestCase();
        testRefs = new HashMap<>();
    }

    @Override
    public void visit(Parameter n, Object arg) {

    }

    @Override
    public void visit(InitializerDeclaration n, Object arg) {

    }

    @Override
    public void visit(JavadocComment n, Object arg) {

    }

    // - Type ----------------------------------------------
    @Override
    public void visit(ClassOrInterfaceType n, Object arg) {
        n.getChildNodes().forEach(cn -> cn.accept(this, arg));
    }

    @Override
    public void visit(PrimitiveType n, Object arg) {

    }

    @Override
    public void visit(ArrayType n, Object arg) {

    }

    @Override
    public void visit(ArrayCreationLevel n, Object arg) {

    }

    @Override
    public void visit(IntersectionType n, Object arg) {

    }

    @Override
    public void visit(UnionType n, Object arg) {

    }

    @Override
    public void visit(VoidType n, Object arg) {

    }

    @Override
    public void visit(WildcardType n, Object arg) {

    }

    @Override
    public void visit(UnknownType n, Object arg) {

    }

    // - Expression ----------------------------------------
    @Override
    public void visit(ArrayAccessExpr n, Object arg) {

    }

    @Override
    public void visit(ArrayCreationExpr n, Object arg) {
        if (n.getLevels().size() > 1) {
            logger.error("array of size > 1 not supported");
            return;
        }

        Class<?> elementType = ParserUtil.getClass(n.getElementType().toString());
        Class<?> type = java.lang.reflect.Array.newInstance(elementType, 0).getClass();
        n.getInitializer().ifPresent(i -> i.accept(this, type));
    }

    @Override
    public void visit(ArrayInitializerExpr n, Object arg) {
        // CHECK
        Class<?> clazz = arg instanceof Class<?> ? (Class<?>) arg
                : arg instanceof ClassOrInterfaceType ? ParserUtil.getClass(arg.toString())
                : null;
        ArrayReference array = null;
        array = new ArrayReference(testCase, clazz);
        s = new ArrayStatement(testCase, array, new int[] { n.getValues().size() });
        r = testCase.addStatement(s);

        int i = 0;
        for (Expression v : n.getValues()) {
            v.accept(this, arg);
            ArrayIndex e = new ArrayIndex(testCase, array, i++);
            s = new AssignmentStatement(testCase, e, r);
            r = testCase.addStatement(s);
        }

        int pos = array.getStPosition();
        s = testCase.getStatement(pos);
        r = testCase.getReturnValue(pos);
    }

    @Override
    public void visit(AssignExpr n, Object arg) {
        if (!(n.getTarget() instanceof NameExpr)) {
            logger.error("assignment not supported target type: " + n.getTarget().getClass());
            return;
        }

        String target = n.getTarget().asNameExpr().getNameAsString();
        VariableReference var = getReference(target);
        n.getValue().accept(this, var.getType());
        s = new AssignmentStatement(testCase, var, r);
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(BinaryExpr n, Object arg) {

    }

    @Override
    public void visit(CastExpr n, Object arg) {

    }

    @Override
    public void visit(ClassExpr n, Object arg) {

    }

    @Override
    public void visit(ConditionalExpr n, Object arg) {

    }

    @Override
    public void visit(EnclosedExpr n, Object arg) {

    }

    @Override
    public void visit(FieldAccessExpr n, Object arg) {

    }

    @Override
    public void visit(InstanceOfExpr n, Object arg) {

    }

    @Override
    public void visit(StringLiteralExpr n, Object arg) {
        s = new StringPrimitiveStatement(testCase, n.getValue());
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(IntegerLiteralExpr n, Object arg) {
        s = new IntPrimitiveStatement(testCase, Integer.parseInt(n.getValue()));
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(LongLiteralExpr n, Object arg) {
        s = new LongPrimitiveStatement(testCase, Long.parseLong(n.getValue()));
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(CharLiteralExpr n, Object arg) {
        s = new CharPrimitiveStatement(testCase, n.getValue().charAt(0));
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(DoubleLiteralExpr n, Object arg) {
        s = new DoublePrimitiveStatement(testCase, Double.parseDouble(n.getValue()));
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(BooleanLiteralExpr n, Object arg) {
        s = new BooleanPrimitiveStatement(testCase, n.getValue());
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(NullLiteralExpr n, Object arg) {
        Class<?> type = arg instanceof Class<?> ? (Class<?>) arg : ParserUtil.getClass(String.valueOf(arg));
        s = new NullStatement(testCase, type);
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
        String name = n.getNameAsString();
        String scope = n.getScope().isPresent() ? n.getScope().get().toString() : ""; // WRONG!!! can be chaining!!!!

        if (scope.isEmpty()) {
            switch (name) {
            case "mock": visitMock(n, arg); return;
            case "when":
            case "verify":
            default:
                logger.error(n.getNameAsString() + ": callee/scope should not be null " + n);
                return;
            }
        }

        if (n.getNameAsString().equals("addVariable")) {
            System.currentTimeMillis();
        }

        VariableReference ref = getReference(scope);
        if (ref == null) {
            System.currentTimeMillis();
        }

        GenericClass clazz = ref == null
                ? ParserUtil.getGenericClass(scope)
                : ref.getGenericClass();
        if (clazz == null) {
            String reason = String.format("%s: %s", CLASS_NOT_FOUND, scope);
            throw new ParseException(reason);
        }

        List<VariableReference> argRefs = new ArrayList<>();
        List<GenericClass> argTypes = new ArrayList<>();
        for (Expression a : n.getArguments()) {
            a.accept(this, arg);
            argRefs.add(r);
            argTypes.add(r.getGenericClass());
        }

        GenericMethod method = ParserUtil.getMethod(clazz, name, argTypes);
        if (method == null) {
            String reason = String.format("%s: %s#%s", METHOD_NOT_FOUND, clazz.getClassName(), name);
            throw new ParseException(reason);
        }

        int paraNum = method.getNumParameters();
        int argNum = argRefs.size();
        if (argNum < paraNum) {
            String reason = String.format("%s: %s#%s", METHOD_NOT_FOUND, clazz.getClassName(), name);
            throw new ParseException(reason);
        }

        argRefs = argRefs.subList(0, paraNum);
        s = new MethodStatement(testCase, method, ref, argRefs);
        r = testCase.addStatement(s);
    }

    public void visitMock(MethodCallExpr n, Object arg) {
        // ParserUtil.getFilePathByClassName("javax.swing.JList");
        assert n.getNameAsString().equals("mock");
        assert n.getArguments().size() == 1;

        String type = n.getArguments().get(0).asClassExpr().getTypeAsString();
        GenericClass clazz = ParserUtil.getGenericClass(type);
        if (clazz == null) {
            String reason = String.format("%s: %s", CLASS_NOT_FOUND, type);
            throw new ParseException(reason);
        }

        Set<GenericConstructor> constructors = ParserUtil.getGenericConstructors(clazz.getRawClass());
        GenericConstructor constructor = null;
        for (GenericConstructor c : constructors) {
            List<VariableReference> paraTypes = new ArrayList<>();
            List<Statement> paraStatements = new ArrayList<>();

            TestCase paraTest = new DefaultTestCase();
            GenericClass paraClass;
            GenericConstructor paraConstructor;
            Statement paraStatement;
            for (java.lang.reflect.Type paraType : c.getParameterTypes()) {
                paraClass = new GenericClass(paraType);
                paraConstructor = new GenericConstructor(c.getConstructor(), paraClass);
                paraStatement = new ConstructorStatement(paraTest, paraConstructor, null);
                System.currentTimeMillis();
            }
        }

        if (constructor == null) {
            String reason = String.format("%s: %s", CONSTRUCTOR_NOT_FOUND, clazz.getClassName());
            throw new ParseException(reason);
        }

        s = new ConstructorStatement(testCase, constructor, null);
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(NameExpr n, Object arg) {
        r = getReference(n.getNameAsString());
    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        String typeStr = n.getTypeAsString();
        Class<?> type = ParserUtil.getClass(typeStr);
        if (type == null) {
            String reason = String.format("%s: %s", CLASS_NOT_FOUND, typeStr);
            throw new ParseException(reason);
        }

        if (n.getArguments().size() != 0) {
            System.currentTimeMillis();
        }

        GenericClass clazz = new GenericClass(type);
        List<VariableReference> argRefs = new ArrayList<>();
        List<GenericClass> argTypes = new ArrayList<>();
        for (Expression a : n.getArguments()) {
            a.accept(this, arg);
            argRefs.add(r);
            argTypes.add(r.getGenericClass());
        }

        GenericConstructor constructor = ParserUtil.getConstructor(clazz, argTypes);
        if (constructor == null) {
            String reason = String.format("%s: %s", CONSTRUCTOR_NOT_FOUND, clazz.getClassName());
            throw new ParseException(reason);
        }

        int paraNum = constructor.getNumParameters();
        int argNum = argRefs.size();
        if (argNum < paraNum) {
            String reason = String.format("%s: %s", CONSTRUCTOR_NOT_FOUND, clazz.getClassName());
            throw new ParseException(reason);
        }

        argRefs = argRefs.subList(0, paraNum);
        s = new ConstructorStatement(testCase, constructor, argRefs);
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(ThisExpr n, Object arg) {

    }

    @Override
    public void visit(SuperExpr n, Object arg) {

    }

    @Override
    public void visit(UnaryExpr n, Object arg) {

    }

    @Override
    public void visit(VariableDeclarationExpr n, Object arg) {
        Type type = n.getElementType();
        n.getVariables().accept(this, type);
    }

    @Override
    public void visit(MarkerAnnotationExpr n, Object arg) {

    }

    @Override
    public void visit(SingleMemberAnnotationExpr n, Object arg) {

    }

    @Override
    public void visit(NormalAnnotationExpr n, Object arg) {

    }

    @Override
    public void visit(MemberValuePair n, Object arg) {

    }

    // - Statements ----------------------------------------
    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Object arg) {

    }

    @Override
    public void visit(LocalClassDeclarationStmt n, Object arg) {

    }

    @Override
    public void visit(LocalRecordDeclarationStmt n, Object arg) {

    }

    @Override
    public void visit(AssertStmt n, Object arg) {

    }

    @Override
    public void visit(BlockStmt n, Object arg) {
        n.getStatements().accept(this, arg);
    }

    @Override
    public void visit(LabeledStmt n, Object arg) {

    }

    @Override
    public void visit(EmptyStmt n, Object arg) {

    }

    @Override
    public void visit(ExpressionStmt n, Object arg) {
        n.getExpression().accept(this, arg);
    }

    @Override
    public void visit(SwitchStmt n, Object arg) {

    }

    @Override
    public void visit(SwitchEntry n, Object arg) {

    }

    @Override
    public void visit(BreakStmt n, Object arg) {

    }

    @Override
    public void visit(ReturnStmt n, Object arg) {

    }

    @Override
    public void visit(IfStmt n, Object arg) {

    }

    @Override
    public void visit(WhileStmt n, Object arg) {

    }

    @Override
    public void visit(ContinueStmt n, Object arg) {

    }

    @Override
    public void visit(DoStmt n, Object arg) {

    }

    @Override
    public void visit(ForEachStmt n, Object arg) {

    }

    @Override
    public void visit(ForStmt n, Object arg) {

    }

    @Override
    public void visit(ThrowStmt n, Object arg) {

    }

    @Override
    public void visit(SynchronizedStmt n, Object arg) {

    }

    @Override
    public void visit(TryStmt n, Object arg) {
        n.getTryBlock().accept(this, arg);
    }

    @Override
    public void visit(CatchClause n, Object arg) {

    }

    @Override
    public void visit(LambdaExpr n, Object arg) {

    }

    @Override
    public void visit(MethodReferenceExpr n, Object arg) {

    }

    @Override
    public void visit(TypeExpr n, Object arg) {

    }

    @Override
    public void visit(NodeList n, Object arg) {
        n.forEach(node -> ((Node) node).accept(this, arg));
    }

    @Override
    public void visit(Name n, Object arg) {
        r = getReference(n.getIdentifier());
    }

    @Override
    public void visit(SimpleName n, Object arg) {
        r = getReference(n.getIdentifier());
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {

    }

    @Override
    public void visit(ModuleDeclaration n, Object arg) {

    }

    @Override
    public void visit(ModuleRequiresDirective n, Object arg) {

    }

    @Override
    public void visit(ModuleExportsDirective n, Object arg) {

    }

    @Override
    public void visit(ModuleProvidesDirective n, Object arg) {

    }

    @Override
    public void visit(ModuleUsesDirective n, Object arg) {

    }

    @Override
    public void visit(ModuleOpensDirective n, Object arg) {

    }

    @Override
    public void visit(UnparsableStmt n, Object arg) {

    }

    @Override
    public void visit(ReceiverParameter n, Object arg) {

    }

    @Override
    public void visit(VarType n, Object arg) {

    }

    @Override
    public void visit(Modifier n, Object arg) {

    }

    @Override
    public void visit(SwitchExpr n, Object arg) {

    }

    @Override
    public void visit(YieldStmt n, Object arg) {

    }

    @Override
    public void visit(TextBlockLiteralExpr n, Object arg) {

    }

    @Override
    public void visit(PatternExpr n, Object arg) {

    }
}
