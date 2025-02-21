package de.paul.compilerbau.parserAST;

import java.util.ArrayList;
import java.util.List;

public class ASTFunctionCallNode extends ASTNode {
    private final String functionName;
    private final List<ASTNode> arguments = new ArrayList<>();

    public ASTFunctionCallNode(String functionName) {
        this.functionName = functionName;
    }

    public void addArgument(ASTNode arg) {
        arguments.add(arg);
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<ASTNode> getArguments() {
        return arguments;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(getIndent(indent) + "FunctionCall: " + functionName + "\n");
        for (ASTNode arg : arguments) {
            sb.append(arg.toString(indent + 1));
        }
        return sb.toString();
    }
}
