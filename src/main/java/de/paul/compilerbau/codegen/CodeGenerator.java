package de.paul.compilerbau.codegen;

import de.paul.compilerbau.parserAST.*;
import java.util.List;

public class CodeGenerator {

    private final InstructionList instructions;
    private final SymbolTable symbolTable;
    private final LabelGenerator labelGenerator;

    public CodeGenerator() {
        this.instructions = new InstructionList();
        this.symbolTable = new SymbolTable();
        this.labelGenerator = new LabelGenerator();
    }

    // Hauptmethode: Startet die Codegenerierung
    public InstructionList generate(ASTNode ast) {
        visit(ast);  // AST traversieren
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
            visit(statement);  // Besuche jedes Statement im Programm
        }
    }

    // --- Assignment Node ---
    private void visitAssignment(ASTAssignmentNode node) {
        visit(node.getExpression());                      // Berechne den Wert (PUSH ...)
        symbolTable.addVariable(node.getVariableName());  // Variable registrieren, falls nicht vorhanden
        instructions.add("STORE", node.getVariableName()); // Wert speichern
    }

    // --- Expression Node (Zahlen & Variablen) ---
    private void visitExpression(ASTExpressionNode node) {
        String value = node.getValue();

        // Prüfen, ob es eine Zahl oder Variable ist
        if (value.matches("-?\\d+")) {  // Zahl
            instructions.add("PUSH", value);
        } else {                        // Variable
            instructions.add("LOAD", value);
        }
    }

    // --- Binary Expression Node ---
    private void visitBinaryExpression(ASTBinaryExpressionNode node) {
        visit(node.getLeft());   // Linken Operanden berechnen
        visit(node.getRight());  // Rechten Operanden berechnen

        // Operatoren-Mapping
        switch (node.getOperator()) {
            case "+" -> instructions.add("ADD");
            case "-" -> instructions.add("SUB");
            case "*" -> instructions.add("MUL");
            case "/" -> instructions.add("DIV");
            case ">" -> instructions.add("GT");
            case "<" -> instructions.add("LT");
            case "==" -> instructions.add("EQ");
            case "!=" -> instructions.add("NEQ");
            case ">=" -> instructions.add("GTE");
            case "<=" -> instructions.add("LTE");
            default -> throw new IllegalArgumentException("Unbekannter Operator: " + node.getOperator());
        }
    }

    // --- Function Definition Node ---
    private void visitFunctionDefinition(ASTFunctionDefinitionNode node) {
        String funcLabel = "FUNC_" + node.getFunctionName();
        instructions.add(funcLabel + ":");

        // Parameter registrieren
        for (String param : node.getParameters()) {
            symbolTable.addVariable(param);
        }

        // Funktionskörper generieren
        for (ASTNode stmt : node.getBodyStatements()) {
            visit(stmt);
        }

        instructions.add("RET"); // Rückkehr nach Funktionsausführung
    }

    // --- Function Call Node ---
    private void visitFunctionCall(ASTFunctionCallNode node) {
        for (ASTNode arg : node.getArguments()) {
            visit(arg);  // Argumente auf den Stack legen
        }
        instructions.add("CALL", node.getFunctionName()); // Funktionsaufruf
    }

    // --- If-Else Node ---
    private void visitIfElse(ASTIfElseNode node) {
        String elseLabel = labelGenerator.generateLabel("ELSE");
        String endLabel = labelGenerator.generateLabel("ENDIF");

        visit(node.getCondition());         // Bedingung evaluieren
        instructions.add("JZ", elseLabel);  // Falls Bedingung false → Springe zu ELSE

        // THEN-Block
        for (ASTNode stmt : node.getIfBody()) {
            visit(stmt);
        }
        instructions.add("JMP", endLabel);  // Nach THEN-Block zum Ende springen

        // ELSE-Block
        instructions.add(elseLabel + ":");
        for (ASTNode stmt : node.getElseBody()) {
            visit(stmt);
        }

        instructions.add(endLabel + ":");   // Ende der If-Else-Struktur
    }

    // --- While Node ---
    private void visitWhile(ASTWhileNode node) {
        String startLabel = labelGenerator.generateLabel("WHILE_START");
        String endLabel = labelGenerator.generateLabel("WHILE_END");

        instructions.add(startLabel + ":"); // Schleifenanfang
        visit(node.getCondition());         // Bedingung evaluieren
        instructions.add("JZ", endLabel);   // Falls false → Schleifenende

        // Schleifenrumpf
        for (ASTNode stmt : node.getBodyStatements()) {
            visit(stmt);
        }

        instructions.add("JMP", startLabel); // Zurück zum Anfang
        instructions.add(endLabel + ":");    // Schleifenende
    }

    // --- Return Node ---
    private void visitReturn(ASTReturnNode node) {
        visit(node.getReturnValue());  // Wert berechnen
        instructions.add("RET");       // Rücksprung
    }
}
