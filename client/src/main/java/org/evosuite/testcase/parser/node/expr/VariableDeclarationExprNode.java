package org.evosuite.testcase.parser.node.expr;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import org.evosuite.testcase.parser.ParserVisitor;
import org.evosuite.testcase.parser.node.AstNode;
import org.evosuite.testcase.parser.node.AstNodeTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VariableDeclarationExprNode implements AstNode {

    private final AstNode name;
    private final AstNode initializer;

    public VariableDeclarationExprNode(VariableDeclarationExpr expr) {
        // TODO: handle assignments
        List<VariableDeclarator> declarators = Arrays.stream(expr.getVariables().toArray(new VariableDeclarator[0]))
                .collect(Collectors.toList());
        VariableDeclarator declarator = declarators.get(0);
        this.name = processName(declarator.getNameAsExpression());
        this.initializer = processInitializer(declarator.getInitializer().get());
    }

    @Override
    public void accept(ParserVisitor visitor) {
        visitor.visit(this);
    }

    private AstNode processName(NameExpr name) {
        return AstNodeTransformer.transform(name);
    }

    private AstNode processInitializer(Expression initializer) {
        return AstNodeTransformer.transform(initializer);
    }

    public AstNode getName() {
        return this.name;
    }

    public AstNode getInitializer() {
        return this.initializer;
    }
}
