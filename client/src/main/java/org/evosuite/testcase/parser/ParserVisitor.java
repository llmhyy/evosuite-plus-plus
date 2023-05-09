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
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ParserVisitor implements VoidVisitor<Object> {

    private final TestCase testCase;
    private final Map<String, VariableReference> references;

    private Statement s;
    private VariableReference r;

    private static final Logger logger = LoggerFactory.getLogger(ParserVisitor.class);

    public ParserVisitor() {
        this.testCase = new DefaultTestCase();
        this.references = new HashMap<>();
    }

    private void updateReferences(String k, VariableReference v) {
        this.references.put(k, v);
    }

    private VariableReference getReference(String k) {
        return this.references.get(k);
    }

    public TestCase getTestCase() {
        return this.testCase;
    }

    public Map<String, VariableReference> getReferences() {
        return this.references;
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

    }

    @Override
    public void visit(VariableDeclarator n, Object arg) {
        // type = (ClassOrInterfaceType) arg
        Optional<Expression> init = n.getInitializer();
        init.ifPresent(i -> i.accept(this, arg));
        r = init.isPresent() ? r : null;
        updateReferences(n.getName().asString(), r);
    }

    @Override
    public void visit(ConstructorDeclaration n, Object arg) {

    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        // i.e. test case
        n.getBody().ifPresent(b -> b.accept(this, arg));
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
        // i.e. test suite
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

    }

    @Override
    public void visit(ArrayInitializerExpr n, Object arg) {

    }

    @Override
    public void visit(AssignExpr n, Object arg) {

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
        Class<?> type = ParserUtil.loadClass(String.valueOf(arg));
        s = new NullStatement(testCase, type);
        r = testCase.addStatement(s);
    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
        if (!n.getScope().isPresent()) {
            logger.error(n.getNameAsString() + ": callee is null");
            return;
        }
        String callee = n.getScope().get().toString();
        String name = n.getNameAsString();
        Class<?> clazz = null;
        List<Class<?>> types = new ArrayList<>();
        if (Objects.equals(name, "add")) {
            clazz = ParserUtil.loadClass("java.util.ArrayList");
            types.add(ParserUtil.loadClass("java.lang.Object"));
        } else if (Objects.equals(name, "toCollection")) {
            clazz = ParserUtil.loadClass("framework.util.ObjectUtils");
            types.add(ParserUtil.loadClass("java.lang.Object"));
        }
        List<VariableReference> args = new ArrayList<>();
        n.getArguments().forEach(a -> {
            a.accept(this, arg);
            args.add(r);
        });
        try {
            if (clazz == null) {
                throw new NoSuchMethodException("clazz is null");
            }
            GenericMethod method = new GenericMethod(
                    clazz.getMethod(name, types.toArray(new Class<?>[0])),
                    clazz);
            s = new MethodStatement(testCase, method, getReference(callee), args);
            r = testCase.addStatement(s);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void visit(NameExpr n, Object arg) {
        r = getReference(n.getNameAsString());
    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        String type = n.getTypeAsString();
        Class<?> clazz = null;
        List<Class<?>> types = new ArrayList<>();
        if (Objects.equals(type, "ArrayList<>")) {
            clazz = ParserUtil.loadClass("java.util.ArrayList");
        }
        List<VariableReference> args = new ArrayList<>();
        n.getArguments().forEach(a -> {
            a.accept(this, arg);
            args.add(r);
        });
        try {
            if (clazz == null) {
                throw new NoSuchMethodException("clazz is null");
            }
            GenericConstructor constructor = new GenericConstructor(
                    clazz.getConstructor(types.toArray(new Class<?>[0])),
                    clazz);
            s = new ConstructorStatement(testCase, constructor, args);
            r = testCase.addStatement(s);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
        }
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
        Type type = n.getElementType().asClassOrInterfaceType();
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
        n.forEach((node) -> ((Node) node).accept(this, arg));
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
