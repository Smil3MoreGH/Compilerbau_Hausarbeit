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
        // 1. Lade den PAUL-Code aus der Datei
        String sourceCode = FileLoader.loadFile("test.paul");

        // 2. Scanner erstellen und Code analysieren
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scan();

        // Scanner-Output ausgeben
        System.out.println("Scanner-Output:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        // 3. Parser ausführen
        ASTParser astParser = new ASTParser(tokens);
        ASTNode ast = astParser.parse();

        // AST ausgeben
        System.out.println("\nParser-Output (AST):");
        System.out.println(ast.toString(0));

        // 4. Codegenerator ausführen
        //CodeGenerator codeGenerator = new CodeGenerator();
        //InstructionList instructions = codeGenerator.generate(ast);

        // Zwischencode ausgeben
        //System.out.println("\nCodegenerator-Output (Zwischencode):");
        //System.out.println(instructions);
    }
}
