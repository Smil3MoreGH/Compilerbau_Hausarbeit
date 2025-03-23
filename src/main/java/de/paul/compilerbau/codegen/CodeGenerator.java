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
        if (node == null) {
            System.out.println("WARNUNG: Besuchter AST-Knoten ist null!");
            return;
        }

        System.out.println("Besuche Knoten vom Typ: " + node.getClass().getSimpleName());

        if (node instanceof ASTProgramNode) visitProgram((ASTProgramNode) node);
        else if (node instanceof ASTAssignmentNode) visitAssignment((ASTAssignmentNode) node);
        else if (node instanceof ASTBinaryExpressionNode) visitBinaryExpression((ASTBinaryExpressionNode) node);
        else if (node instanceof ASTExpressionNode) visitExpression((ASTExpressionNode) node);
        else if (node instanceof ASTFunctionDefinitionNode) visitFunctionDefinition((ASTFunctionDefinitionNode) node);
        else if (node instanceof ASTFunctionCallNode) visitFunctionCall((ASTFunctionCallNode) node);
        else if (node instanceof ASTIfElseNode) visitIfElse((ASTIfElseNode) node);
        else if (node instanceof ASTWhileNode) visitWhile((ASTWhileNode) node);
        else if (node instanceof ASTReturnNode) visitReturn((ASTReturnNode) node);
        else {
            System.out.println("Unbekannter AST-Knoten: " + node);
        }
    }

    // --- Program Node ---
    private void visitProgram(ASTProgramNode node) {
        for (ASTNode statement : node.getStatements()) {
            visit(statement);
        }
    }

    // --- Assignment Node ---
    private void visitAssignment(ASTAssignmentNode node) {
        System.out.println("Verarbeite Zuweisung an Variable: " + node.getVariableName());
        visitTo(currentTarget, node.getExpression());
        symbolTable.addVariable(node.getVariableName());
        currentTarget.add("STORE", node.getVariableName());
    }

    private void visitExpression(ASTExpressionNode node) {
        String value = node.getValue();
        System.out.println("Verarbeite Expression: " + value);
        InstructionList target = getCurrentInstructionList();

        if (value.matches("-?\\d+")) {
            target.add("PUSH", value);
        } else {
            target.add("LOAD", value);
        }
    }

    private void visitBinaryExpression(ASTBinaryExpressionNode node) {
        System.out.println("Verarbeite binären Ausdruck: " + node.getOperator());
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

    private void visitFunctionDefinition(ASTFunctionDefinitionNode node) {
        System.out.println("Definiere Funktion: " + node.getFunctionName());
        InstructionList oldTarget = currentTarget;
        currentTarget = functionInstructions;

        String funcLabel = "FUNC_" + node.getFunctionName();
        functionInstructions.add(funcLabel + ":");

        List<String> params = node.getParameters();
        System.out.println("Funktionsparameter: " + params);
        for (int i = params.size() - 1; i >= 0; i--) {
            functionInstructions.add("STORE", params.get(i));
        }

        boolean hasReturn = false;
        for (ASTNode stmt : node.getBodyStatements()) {
            visitTo(currentTarget, stmt);
            if (stmt instanceof ASTReturnNode) hasReturn = true;
        }

        if (!hasReturn) {
            functionInstructions.add("RET");
        }

        currentTarget = oldTarget;
    }

    private void visitFunctionCall(ASTFunctionCallNode node) {
        System.out.println("Funktionsaufruf: " + node.getFunctionName() + " mit Argumenten: " + node.getArguments().size());
        for (ASTNode arg : node.getArguments()) {
            visitTo(mainInstructions, arg);
        }
        mainInstructions.add("CALL", node.getFunctionName());
    }

    private void visitIfElse(ASTIfElseNode node) {
        System.out.println("Verarbeite If-Else-Struktur");
        InstructionList target = getCurrentInstructionList();

        String elseLabel = labelGenerator.generateLabel("ELSE");
        String endLabel = labelGenerator.generateLabel("ENDIF");

        visitTo(target, node.getCondition());
        target.add("JZ", elseLabel);

        System.out.println("If-Block:");
        for (ASTNode stmt : node.getIfBody()) {
            visitTo(target, stmt);
        }

        target.add("JMP", endLabel);

        target.add(elseLabel + ":");
        System.out.println("Else-Block:");
        for (ASTNode stmt : node.getElseBody()) {
            visitTo(target, stmt);
        }

        target.add(endLabel + ":");
    }

    private void visitWhile(ASTWhileNode node) {
        System.out.println("Verarbeite While-Schleife");
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

    private void visitReturn(ASTReturnNode node) {
        System.out.println("Verarbeite Rückgabe");
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
