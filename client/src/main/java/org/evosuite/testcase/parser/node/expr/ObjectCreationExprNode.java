package org.evosuite.testcase.parser.node.expr;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.evosuite.testcase.parser.ParserVisitor;
import org.evosuite.testcase.parser.node.AstNode;
import org.evosuite.testcase.parser.node.AstNodeTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectCreationExprNode implements AstNode {

    private final AstNode type;
    private final List<AstNode> arguments;

    public ObjectCreationExprNode(ObjectCreationExpr expr) {
        this.type = processObject(expr.getType().getNameAsExpression());
        this.arguments = processArguments(expr.getArguments());
    }

    private AstNode processObject(NameExpr object) {
        return AstNodeTransformer.transform(object);
    }

    private List<AstNode> processArguments(NodeList<Expression> args) {
        return Arrays.stream(args.toArray(new Expression[0]))
                .map(AstNodeTransformer::transform)
                .collect(Collectors.toList());
    }

    @Override
    public void accept(ParserVisitor visitor) {
        visitor.visit(this);
    }

    public AstNode getType() {
        return this.type;
    }

    public List<AstNode> getArguments() {
        return this.arguments;
    }
}
