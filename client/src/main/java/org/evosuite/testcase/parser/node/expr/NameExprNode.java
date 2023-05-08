package org.evosuite.testcase.parser.node.expr;

import com.github.javaparser.ast.expr.NameExpr;
import org.evosuite.testcase.parser.ParserVisitor;
import org.evosuite.testcase.parser.node.AstNode;

public class NameExprNode implements AstNode {

    private final String name;

    public NameExprNode(NameExpr expr) {
        this.name = expr.getNameAsString();
    }

    @Override
    public void accept(ParserVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return this.name;
    }
}
