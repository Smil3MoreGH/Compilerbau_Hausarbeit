package de.paul.compilerbau.codegen;

import de.paul.compilerbau.parserAST.*;
import java.util.List;

/**
 * Generiert Bytecode-ähnliche Instruktionen aus einem abstrakten Syntaxbaum (AST).
 */
public class CodeGenerator {

    private final InstructionList instructions;         // Gesamtliste aller generierten Instruktionen
    private final InstructionList functionInstructions; // Instruktionen für Funktionen
    private final InstructionList mainInstructions;     // Instruktionen für das Hauptprogramm
    private InstructionList currentTarget;              // Aktuell verwendete Liste für Codegenerierung
    private final SymbolTable symbolTable;              // Symboltabelle für Variablen
    private final LabelGenerator labelGenerator;        // Erzeugt eindeutige Sprungmarken (Labels)

    public CodeGenerator() {
        this.instructions = new InstructionList();
        this.functionInstructions = new InstructionList();
        this.mainInstructions = new InstructionList();
        this.currentTarget = mainInstructions;
        this.symbolTable = new SymbolTable();
        this.labelGenerator = new LabelGenerator();
    }

    /**
     * Einstiegspunkt: Erzeugt aus einem AST die vollständige Befehlsliste.
     */
    public InstructionList generate(ASTNode ast) {
        visit(ast);  // AST traversieren

        // Struktur: Erst GOTO zu main, dann Funktionen, dann Hauptcode
        instructions.add("GOTO", "START");
        instructions.addAll(functionInstructions);
        instructions.add("START:");
        instructions.addAll(mainInstructions);

        return instructions;
    }

    /**
     * Dispatcher: Leitet die Verarbeitung je nach Knotentyp an die passende Methode weiter.
     */
    private void visit(ASTNode node) {
        if (node == null) {
            System.out.println("WARNUNG: Besuchter AST-Knoten ist null!");
            return;
        }

        switch (node) {
            case ASTProgramNode astProgramNode -> visitProgram(astProgramNode);
            case ASTAssignmentNode astAssignmentNode -> visitAssignment(astAssignmentNode);
            case ASTBinaryExpressionNode astBinaryExpressionNode -> visitBinaryExpression(astBinaryExpressionNode);
            case ASTExpressionNode astExpressionNode -> visitExpression(astExpressionNode);
            case ASTFunctionDefinitionNode astFunctionDefinitionNode -> visitFunctionDefinition(astFunctionDefinitionNode);
            case ASTFunctionCallNode astFunctionCallNode -> visitFunctionCall(astFunctionCallNode);
            case ASTIfElseNode astIfElseNode -> visitIfElse(astIfElseNode);
            case ASTWhileNode astWhileNode -> visitWhile(astWhileNode);
            case ASTReturnNode astReturnNode -> visitReturn(astReturnNode);
            default -> System.out.println("Unbekannter AST-Knoten: " + node);
        }
    }

    // --- Knoten-Verarbeitungsmethoden ---

    private void visitProgram(ASTProgramNode node) {
        for (ASTNode statement : node.getStatements()) {
            visit(statement);
        }
    }

    private void visitAssignment(ASTAssignmentNode node) {
        visitTo(currentTarget, node.getExpression());
        symbolTable.addVariable(node.getVariableName());
        currentTarget.add("STORE", node.getVariableName());
    }

    private void visitExpression(ASTExpressionNode node) {
        String value = node.getValue();
        InstructionList target = getCurrentInstructionList();

        if (value.matches("-?\\d+")) {
            target.add("PUSH", value);  // Literal-Zahl
        } else {
            target.add("LOAD", value);  // Variable
        }
    }

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

    private void visitFunctionDefinition(ASTFunctionDefinitionNode node) {
        InstructionList oldTarget = currentTarget;
        currentTarget = functionInstructions;

        String funcLabel = "FUNC_" + node.getFunctionName();
        functionInstructions.add(funcLabel + ":");

        // Übergabeparameter von oben nach unten auf den Stack schreiben
        List<String> params = node.getParameters();
        for (int i = params.size() - 1; i >= 0; i--) {
            functionInstructions.add("STORE", params.get(i));
        }

        boolean hasReturn = false;
        for (ASTNode stmt : node.getBodyStatements()) {
            visitTo(currentTarget, stmt);
            if (stmt instanceof ASTReturnNode) hasReturn = true;
        }

        // Füge implizites RET hinzu, falls kein Return vorhanden
        if (!hasReturn) {
            functionInstructions.add("RET");
        }

        currentTarget = oldTarget;
    }

    private void visitFunctionCall(ASTFunctionCallNode node) {
        for (ASTNode arg : node.getArguments()) {
            visitTo(currentTarget, arg); // statt mainInstructions
        }
        currentTarget.add("CALL", node.getFunctionName()); // statt mainInstructions
    }

    private void visitIfElse(ASTIfElseNode node) {
        InstructionList target = getCurrentInstructionList();

        String elseLabel = labelGenerator.generateLabel("ELSE");
        String endLabel = labelGenerator.generateLabel("ENDIF");

        visitTo(target, node.getCondition());
        target.add("JZ", elseLabel);  // Springe zu else, wenn Bedingung false

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

    private void visitWhile(ASTWhileNode node) {
        InstructionList target = getCurrentInstructionList();

        String startLabel = labelGenerator.generateLabel("WHILE_START");
        String endLabel = labelGenerator.generateLabel("WHILE_END");

        target.add(startLabel + ":");
        visitTo(target, node.getCondition());
        target.add("JZ", endLabel);  // Beende Schleife, wenn Bedingung false

        for (ASTNode stmt : node.getBodyStatements()) {
            visitTo(target, stmt);
        }

        target.add("JMP", startLabel);  // Rücksprung zum Schleifenanfang
        target.add(endLabel + ":");
    }

    private void visitReturn(ASTReturnNode node) {
        InstructionList target = getCurrentInstructionList();
        visitTo(target, node.getReturnValue());
        target.add("RET");
    }

    // --- Hilfsmethoden ---

    // Gibt das aktuelle Ziel für die Codegenerierung zurück
    private InstructionList getCurrentInstructionList() {
        return currentTarget;
    }

    // Temporär anderes Ziel setzen, z.B. für Funktionsaufrufe oder Bedingungen
    private void visitTo(InstructionList target, ASTNode node) {
        InstructionList oldTarget = currentTarget;
        currentTarget = target;
        visit(node);
        currentTarget = oldTarget;
    }
}
