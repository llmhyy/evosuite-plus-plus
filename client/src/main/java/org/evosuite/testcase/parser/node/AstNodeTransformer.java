package org.evosuite.testcase.parser.node;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import org.evosuite.testcase.parser.ParserVisitor;
import org.evosuite.testcase.parser.node.expr.*;

import java.util.List;
import java.util.Objects;

public class AstNodeTransformer {

    public static AstNode transform(Node node) {
        String type = node.getClass().getSimpleName();
        switch (type) {
            case "NameExpr":
                return new NameExprNode((NameExpr) node);
            case "IntegerLiteralExpr":
                return new IntegerLiteralExprNode((IntegerLiteralExpr) node);
            case "StringLiteralExpr":
                return new StringLiteralExprNode((StringLiteralExpr) node);
            case "VariableDeclarationExpr":
                return new VariableDeclarationExprNode((VariableDeclarationExpr) node);
            case "ObjectCreationExpr":
                return new ObjectCreationExprNode((ObjectCreationExpr) node);
            case "MethodCallExpr":
                return new MethodCallExprNode((MethodCallExpr) node);
            default:
                System.out.println(type + " not supported for transformation");
                return null;
        }
    }
}
