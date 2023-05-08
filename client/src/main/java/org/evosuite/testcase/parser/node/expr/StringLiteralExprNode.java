package org.evosuite.testcase.parser.node.expr;

import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.evosuite.testcase.parser.ParserVisitor;
import org.evosuite.testcase.parser.node.AstNode;

public class StringLiteralExprNode implements AstNode {

    private final String value;

    public StringLiteralExprNode(StringLiteralExpr expr) {
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
