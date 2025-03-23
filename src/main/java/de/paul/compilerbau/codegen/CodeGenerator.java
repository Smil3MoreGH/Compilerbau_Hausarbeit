package de.paul.compilerbau.codegen;

import de.paul.compilerbau.parserAST.*;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private final InstructionList instructions;
    private final InstructionList functionInstructions;
    private final InstructionList mainInstructions;
    private InstructionList currentTarget;
    private final SymbolTable symbolTable;
    private final LabelGenerator labelGenerator;

    public CodeGenerator() {
        this.instructions = new InstructionList();
        this.functionInstructions = new InstructionList();
        this.mainInstructions = new InstructionList();
        this.currentTarget = mainInstructions; // Jetzt ist mainInstructions schon da
        this.symbolTable = new SymbolTable();
        this.labelGenerator = new LabelGenerator();
    }

    // Hauptmethode: Startet die Codegenerierung
    public InstructionList generate(ASTNode ast) {
        visit(ast);  // AST traversieren

        // Struktur: GOTO START → alle Funktionen → START: → Hauptprogramm
        instructions.add("GOTO", "START");
        instructions.addAll(functionInstructions);
        instructions.add("START:");
        instructions.addAll(mainInstructions);

        return instructions;
    }

    // Dispatcher: Ermittelt den Typ des AST-Knotens
    private void visit(ASTNode node) {
        if (node instanceof ASTProgramNode) visitProgram((ASTProgramNode) node);
        else if (node instanceof ASTAssignmentNode) visitAssignment((ASTAssignmentNode) node);
        else if (node instanceof ASTBinaryExpressionNode) visitBinaryExpression((ASTBinaryExpressionNode) node);
        else if (node instanceof ASTExpressionNode) visitExpression((ASTExpressionNode) node);
        else if (node instanceof ASTFunctionDefinitionNode) visitFunctionDefinition((ASTFunctionDefinitionNode) node);
        else if (node instanceof ASTFunctionCallNode) visitFunctionCall((ASTFunctionCallNode) node);
        else if (node instanceof ASTIfElseNode) visitIfElse((ASTIfElseNode) node);
        else if (node instanceof ASTWhileNode) visitWhile((ASTWhileNode) node);
        else if (node instanceof ASTReturnNode) visitReturn((ASTReturnNode) node);
    }

    // --- Program Node ---
    private void visitProgram(ASTProgramNode node) {
        for (ASTNode statement : node.getStatements()) {
            visit(statement);
        }
    }

    // --- Assignment Node ---
    private void visitAssignment(ASTAssignmentNode node) {
        visitTo(mainInstructions, node.getExpression());
        symbolTable.addVariable(node.getVariableName());
        mainInstructions.add("STORE", node.getVariableName());
    }

    // --- Expression Node ---
    private void visitExpression(ASTExpressionNode node) {
        String value = node.getValue();
        InstructionList target = getCurrentInstructionList();

        if (value.matches("-?\\d+")) {
            target.add("PUSH", value);
        } else {
            target.add("LOAD", value);
        }
    }

    // --- Binary Expression Node ---
    private void visitBinaryExpression(ASTBinaryExpressionNode node) {
        InstructionList target = getCurrentInstructionList();

        visitTo(target, node.getLeft());
        visitTo(target, node.getRight());

        switch (node.getOperator()) {
            case "+" -> target.add("ADD");
            case "-" -> target.add("SUB");
            case "*" -> target.add("MUL");
            case "/" -> target.add("DIV");
            case ">" -> target.add("GT");
            case "<" -> target.add("LT");
            case "==" -> target.add("EQ");
            case "!=" -> target.add("NEQ");
            case ">=" -> target.add("GTE");
            case "<=" -> target.add("LTE");
            default -> throw new IllegalArgumentException("Unbekannter Operator: " + node.getOperator());
        }
    }

    // --- Function Definition Node ---
    private void visitFunctionDefinition(ASTFunctionDefinitionNode node) {
        InstructionList oldTarget = currentTarget;
        currentTarget = functionInstructions;

        String funcLabel = "FUNC_" + node.getFunctionName();
        functionInstructions.add(funcLabel + ":");

        List<String> params = node.getParameters();
        for (int i = params.size() - 1; i >= 0; i--) {
            functionInstructions.add("STORE", params.get(i));
        }

        boolean hasReturn = false;
        for (ASTNode stmt : node.getBodyStatements()) {
            visit(stmt);
            if (stmt instanceof ASTReturnNode) hasReturn = true;
        }

        if (!hasReturn) {
            functionInstructions.add("RET");
        }

        currentTarget = oldTarget;
    }

    // --- Function Call Node ---
    private void visitFunctionCall(ASTFunctionCallNode node) {
        for (ASTNode arg : node.getArguments()) {
            visitTo(mainInstructions, arg);
        }
        mainInstructions.add("CALL", node.getFunctionName());
    }

    // --- If-Else Node ---
    private void visitIfElse(ASTIfElseNode node) {
        InstructionList target = getCurrentInstructionList();

        String elseLabel = labelGenerator.generateLabel("ELSE");
        String endLabel = labelGenerator.generateLabel("ENDIF");

        visitTo(target, node.getCondition());
        target.add("JZ", elseLabel);

        for (ASTNode stmt : node.getIfBody()) {
            visitTo(target, stmt);
        }

        target.add("JMP", endLabel);

        target.add(elseLabel + ":");
        for (ASTNode stmt : node.getElseBody()) {
            visitTo(target, stmt);
        }

        target.add(endLabel + ":");
    }

    // --- While Node ---
    private void visitWhile(ASTWhileNode node) {
        InstructionList target = getCurrentInstructionList();

        String startLabel = labelGenerator.generateLabel("WHILE_START");
        String endLabel = labelGenerator.generateLabel("WHILE_END");

        target.add(startLabel + ":");
        visitTo(target, node.getCondition());
        target.add("JZ", endLabel);

        for (ASTNode stmt : node.getBodyStatements()) {
            visitTo(target, stmt);
        }

        target.add("JMP", startLabel);
        target.add(endLabel + ":");
    }

    // --- Return Node ---
    private void visitReturn(ASTReturnNode node) {
        InstructionList target = getCurrentInstructionList();
        visitTo(target, node.getReturnValue());
        target.add("RET");
    }

    // --- Hilfsmethoden ---

    private InstructionList getCurrentInstructionList() {
        return currentTarget;
    }

    private void visitTo(InstructionList target, ASTNode node) {
        InstructionList oldTarget = currentTarget;
        currentTarget = target;
        visit(node);
        currentTarget = oldTarget;
    }
}
