package de.paul.compilerbau;

import de.paul.compilerbau.scanner.Scanner;
import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.utils.FileLoader;
import de.paul.compilerbau.parserAST.ASTParser;
import de.paul.compilerbau.parserAST.ASTNode;
import de.paul.compilerbau.codegen.CodeGenerator;
import de.paul.compilerbau.codegen.InstructionList;
import de.paul.compilerbau.vm.VirtualMachine;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Code laden
        String sourceCode = FileLoader.loadFile("rekursive_funktion.paul");

        // Scanner: Code → Tokens
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scan();

        // Parser: Tokens → AST
        ASTParser parser = new ASTParser(tokens);
        ASTNode ast = parser.parse();

        // AST anzeigen
        System.out.println("\nParser-Output (AST):");
        System.out.println(ast.toString(0));

        // CodeGenerator: AST → InstructionList
        CodeGenerator codeGenerator = new CodeGenerator();
        InstructionList instructions = codeGenerator.generate(ast);

        // Zwischencode ausgeben
        System.out.println("\nCodegenerator-Output (Zwischencode):");
        System.out.println(instructions);

        // Virtuelle Maschine: Instructions → Ausführung
        System.out.println("\nStarte Virtuelle Maschine...");
        VirtualMachine vm = new VirtualMachine(instructions);
        vm.run();

        System.out.println("Programm erfolgreich ausgeführt.");
    }
}
