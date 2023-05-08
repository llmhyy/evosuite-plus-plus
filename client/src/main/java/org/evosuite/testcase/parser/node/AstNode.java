package org.evosuite.testcase.parser.node;

import org.evosuite.testcase.parser.ParserVisitor;

public interface AstNode {

    void accept(ParserVisitor visitor);
}
