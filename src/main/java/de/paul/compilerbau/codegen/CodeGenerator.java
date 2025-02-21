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
        visit(ast);  // Traversiert den AST
        return instructions;
    }

    // Dispatcher: Ermittelt den AST-Knotentyp
    private void visit(ASTNode node) {
        if (node instanceof ASTProgramNode) visitProgram((ASTProgramNode) node);
        else if (node instanceof ASTAssignmentNode) visitAssignment((ASTAssignmentNode) node);
        else if (node instanceof ASTBinaryExpressionNode) visitBinaryExpression((ASTBinaryExpressionNode) node);
        else if (node instanceof ASTFunctionDefinitionNode) visitFunctionDefinition((ASTFunctionDefinitionNode) node);
        else if (node instanceof ASTFunctionCallNode) visitFunctionCall((ASTFunctionCallNode) node);
        else if (node instanceof ASTIfElseNode) visitIfElse((ASTIfElseNode) node);
        else if (node instanceof ASTWhileNode) visitWhile((ASTWhileNode) node);
        else if (node instanceof ASTReturnNode) visitReturn((ASTReturnNode) node);
        else if (node instanceof ASTExpressionNode) visitExpression((ASTExpressionNode) node); // Generischer Ausdruck
    }

    // --- Program Node ---
    private void visitProgram(ASTProgramNode node) {
        for (ASTNode statement : node.getStatements()) {
            visit(statement);  // Besuche jedes Statement im Programm
        }
    }

    // --- Assignment Node ---
    private void visitAssignment(ASTAssignmentNode node) {
        visit(node.getExpression());               // Berechne den Wert (PUSH ...)
        symbolTable.addVariable(node.getVariableName()); // Variable registrieren
        instructions.add("STORE", node.getVariableName()); // Wert speichern
    }

    // --- Binary Expression Node ---
    private void visitBinaryExpression(ASTBinaryExpressionNode node) {
        visit(node.getLeft());   // Linken Operand berechnen (PUSH ...)
        visit(node.getRight());  // Rechten Operand berechnen (PUSH ...)

        // Operatoren-Handling
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
        }
    }

    // --- Function Definition Node ---
    private void visitFunctionDefinition(ASTFunctionDefinitionNode node) {
        String funcLabel = "FUNC_" + node.getFunctionName();
        instructions.add(funcLabel + ":");

        // Parameter in Symboltabelle eintragen
        for (String param : node.getParameters()) {
            symbolTable.addVariable(param);
        }

        visit(node.getBody()); // Funktionskörper generieren
        instructions.add("RET"); // Rückkehr am Ende der Funktion
    }

    // --- Function Call Node ---
    private void visitFunctionCall(ASTFunctionCallNode node) {
        for (ASTExpressionNode arg : node.getArguments()) {
            visit(arg); // Argumente auf den Stack legen
        }
        instructions.add("CALL", node.getFunctionName()); // Funktionsaufruf
    }

    // --- If-Else Node ---
    private void visitIfElse(ASTIfElseNode node) {
        String elseLabel = labelGenerator.generateLabel("ELSE");
        String endLabel = labelGenerator.generateLabel("ENDIF");

        visit(node.getCondition());         // Bedingung evaluieren
        instructions.add("JZ", elseLabel);  // Falls false → springe zum Else-Block

        visit(node.getThenBlock());         // Then-Block generieren
        instructions.add("JMP", endLabel);  // Überspringe Else nach Then

        instructions.add(elseLabel + ":");  // Else-Label
        if (node.getElseBlock() != null) {
            visit(node.getElseBlock());     // Else-Block generieren
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

        visit(node.getBody());              // Schleifenrumpf generieren
        instructions.add("JMP", startLabel);// Wieder an den Anfang springen

        instructions.add(endLabel + ":");   // Schleifenende
    }

    // --- Return Node ---
    private void visitReturn(ASTReturnNode node) {
        visit(node.getExpression());  // Wert berechnen
        instructions.add("RET");      // Rücksprung zur aufrufenden Funktion
    }

    // --- Generischer Ausdruck (Zahlen, Variablen, Funktionsaufrufe) ---
    private void visitExpression(ASTExpressionNode node) {
        if (node instanceof ASTBinaryExpressionNode) {
            visitBinaryExpression((ASTBinaryExpressionNode) node);
        } else if (node instanceof ASTFunctionCallNode) {
            visitFunctionCall((ASTFunctionCallNode) node);
        } else if (node instanceof ASTVariableNode variableNode) {
            instructions.add("LOAD", variableNode.getVariableName());
        } else if (node instanceof ASTNumberNode numberNode) {
            instructions.add("PUSH", String.valueOf(numberNode.getValue()));
        }
    }
}
