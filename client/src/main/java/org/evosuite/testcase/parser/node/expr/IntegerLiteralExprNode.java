package org.evosuite.testcase.parser.node.expr;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import org.evosuite.testcase.parser.ParserVisitor;
import org.evosuite.testcase.parser.node.AstNode;

public class IntegerLiteralExprNode implements AstNode {

    private final String value;

    public IntegerLiteralExprNode(IntegerLiteralExpr expr) {
        this.value = expr.getValue();
    }

    @Override
    public void accept(ParserVisitor visitor) {
        visitor.visit(this);
    }

    public String getValue() {
        return this.value;
    }
}
