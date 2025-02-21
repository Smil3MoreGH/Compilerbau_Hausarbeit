package de.paul.compilerbau.parserAST;

import java.util.ArrayList;
import java.util.List;

public class ASTFunctionDefinitionNode extends ASTNode {
    private final String functionName;
    private final List<String> parameters = new ArrayList<>();
    private final List<ASTNode> bodyStatements = new ArrayList<>();

    public ASTFunctionDefinitionNode(String functionName) {
        this.functionName = functionName;
    }

    public void addParameter(String param) {
        parameters.add(param);
    }

    public void addBodyStatement(ASTNode stmt) {
        bodyStatements.add(stmt);
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<ASTNode> getBodyStatements() {
        return bodyStatements;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(getIndent(indent) + "Function: " + functionName + "(" + String.join(", ", parameters) + ")\n");
        for (ASTNode stmt : bodyStatements) {
            sb.append(stmt.toString(indent + 1));
        }
        return sb.toString();
    }
}
