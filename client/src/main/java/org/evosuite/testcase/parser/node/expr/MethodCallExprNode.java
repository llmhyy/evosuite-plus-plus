package org.evosuite.testcase.parser.node.expr;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import org.evosuite.testcase.parser.ParserVisitor;
import org.evosuite.testcase.parser.node.AstNode;
import org.evosuite.testcase.parser.node.AstNodeTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodCallExprNode implements AstNode {

    private final AstNode callee;
    private final AstNode name;
    private final List<AstNode> arguments;

    public MethodCallExprNode(MethodCallExpr expr) {
        assert expr.getScope().isPresent();
        this.callee = processCallee(expr.getScope().get());
        this.name = processName(expr.getNameAsExpression());
        this.arguments = processArguments(expr.getArguments());
    }

    @Override
    public void accept(ParserVisitor visitor) {
        visitor.visit(this);
    }

    private AstNode processCallee(Expression callee) {
        return AstNodeTransformer.transform(callee);
    }

    private AstNode processName(NameExpr name) {
        return AstNodeTransformer.transform(name);
    }

    private List<AstNode> processArguments(NodeList<Expression> args) {
        return Arrays.stream(args.toArray(new Expression[0]))
                .map(AstNodeTransformer::transform)
                .collect(Collectors.toList());
    }

    public AstNode getCallee() {
        return this.callee;
    }

    public AstNode getName() {
        return this.name;
    }

    public List<AstNode> getArguments() {
        return this.arguments;
    }
}
