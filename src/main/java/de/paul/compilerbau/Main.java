package de.paul.compilerbau;

import de.paul.compilerbau.scanner.Scanner;
import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.utils.FileLoader;
import de.paul.compilerbau.parserAST.ASTParser;
import de.paul.compilerbau.parserAST.ASTNode;
import de.paul.compilerbau.codegen.CodeGenerator;
import de.paul.compilerbau.codegen.InstructionList;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Code laden
        String sourceCode = FileLoader.loadFile("test.paul");

        // Scanner & Tokens
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scan();

        // Parser → AST
        ASTParser parser = new ASTParser(tokens);
        ASTNode ast = parser.parse();

        // AST anzeigen
        System.out.println("\nParser-Output (AST):");
        System.out.println(ast.toString(0));

        // Code generieren
        CodeGenerator codeGenerator = new CodeGenerator();
        InstructionList instructions = codeGenerator.generate(ast);

        // Zwischencode anzeigen
        System.out.println("\nCodegenerator-Output (Zwischencode):");
        System.out.println(instructions);
    }
}
