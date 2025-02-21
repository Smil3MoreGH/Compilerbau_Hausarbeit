package de.paul.compilerbau.vm;

import de.paul.compilerbau.codegen.Instruction;
import de.paul.compilerbau.codegen.InstructionList;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VirtualMachine {
    private final Stack<Integer> stack = new Stack<>();
    private final Map<String, Integer> memory = new HashMap<>(); // Variablen-Speicher
    private final Stack<ExecutionContext> callStack = new Stack<>();
    private final InstructionList instructions;
    private int instructionPointer = 0; // Speichert die aktuelle Instruktionsposition

    public VirtualMachine(InstructionList instructions) {
        this.instructions = instructions;
    }

    // Startet die VM
    public void run() {
        System.out.println("Starte Ausführung der VM");
        while (instructionPointer < instructions.getInstructions().size()) {
            Instruction instruction = instructions.getInstructions().get(instructionPointer);
            System.out.println("Ausführung von Instruktion bei Position " + instructionPointer + ": " + instruction.getOpcode() + " " + instruction.getOperand());
            execute(instruction);
            instructionPointer++; // Nächste Instruktion
            System.out.println("Aktueller Stack: " + stack);
            System.out.println("Aktueller Speicher: " + memory);
            System.out.println("Aktueller Call-Stack: " + callStack);
            System.out.println("----------------------------------------");
        }
    }

    private void execute(Instruction instruction) {
        String opcode = instruction.getOpcode();
        String operand = instruction.getOperand();

        // Labels ignorieren (z.B. FUNC_add:, ELSE_0:)
        if (opcode.endsWith(":")) {
            return; // Labels sind keine ausführbaren Instruktionen
        }

        switch (opcode) {
            case InstructionSet.PUSH -> stack.push(Integer.parseInt(operand));
            case InstructionSet.LOAD -> {
                if (!memory.containsKey(operand)) {
                    throw new RuntimeException("Variable '" + operand + "' ist nicht definiert!");
                }
                stack.push(memory.get(operand));
            }
            case InstructionSet.STORE -> memory.put(operand, stack.pop());
            case InstructionSet.ADD -> stack.push(stack.pop() + stack.pop());
            case InstructionSet.SUB -> stack.push(-stack.pop() + stack.pop());
            case InstructionSet.MUL -> stack.push(stack.pop() * stack.pop());
            case InstructionSet.DIV -> {
                int divisor = stack.pop();
                int dividend = stack.pop();
                if (divisor == 0) throw new RuntimeException("Division durch Null!");
                stack.push(dividend / divisor);
            }

            case InstructionSet.GT -> stack.push(stack.pop() < stack.pop() ? 1 : 0);
            case InstructionSet.LT -> stack.push(stack.pop() > stack.pop() ? 1 : 0);
            case InstructionSet.EQ -> stack.push(stack.pop().equals(stack.pop()) ? 1 : 0);
            case InstructionSet.NEQ -> stack.push(!stack.pop().equals(stack.pop()) ? 1 : 0);
            case InstructionSet.GTE -> stack.push(stack.pop() <= stack.pop() ? 1 : 0);
            case InstructionSet.LTE -> stack.push(stack.pop() >= stack.pop() ? 1 : 0);

            case InstructionSet.JMP -> instructionPointer = findLabel(operand);
            case InstructionSet.JZ -> {
                if (stack.pop() == 0) instructionPointer = findLabel(operand);
            }
            case InstructionSet.CALL -> {
                callStack.push(new ExecutionContext(instructionPointer)); // Rücksprungadresse speichern
                instructionPointer = findLabel(operand); // Springe zu Funktionslabel
            }
            case InstructionSet.RET -> {
                if (callStack.isEmpty()) {
                    throw new RuntimeException("RET-Fehler: Kein Rücksprung möglich!");
                }
                instructionPointer = callStack.pop().getReturnAddress();
            }

            default -> throw new RuntimeException("Unbekannte Instruktion: " + opcode);
        }
    }

    // Sucht eine Label-Position
    private int findLabel(String label) {
        for (int i = 0; i < instructions.getInstructions().size(); i++) {
            if (instructions.getInstructions().get(i).getOpcode().equals(label + ":")) {
                return i;
            }
        }
        throw new RuntimeException("Label '" + label + "' nicht gefunden!");
    }
}
