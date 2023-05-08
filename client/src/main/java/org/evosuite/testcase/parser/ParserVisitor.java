package org.evosuite.testcase.parser;

import com.github.javaparser.ast.Node;
import org.evosuite.testcase.parser.node.expr.*;

public interface ParserVisitor {

    void visit(NameExprNode node);
    void visit(IntegerLiteralExprNode node);
    void visit(StringLiteralExprNode node);

    void visit(VariableDeclarationExprNode node);

    void visit(ObjectCreationExprNode node);
    void visit(MethodCallExprNode node);
}
