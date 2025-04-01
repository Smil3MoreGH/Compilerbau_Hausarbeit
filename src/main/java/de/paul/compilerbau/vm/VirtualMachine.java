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
        //System.out.println("=== Starte Ausführung der Virtuellen Maschine ===\n");

        // 1. Label-Tabelle vorbereiten (für Sprünge)
        Map<String, Integer> labelMap = new HashMap<>();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instr = instructions.get(i);
            if (instr.getOpcode().endsWith(":")) {
                String label = instr.getOpcode().substring(0, instr.getOpcode().length() - 1); // ":" abschneiden
                labelMap.put(label, i);
            }
        }

        // 2. Start Haupt-Loop
        while (instructionPointer < instructions.size()) {
            Instruction instr = instructions.get(instructionPointer);
            instructionPointer++; // Normaler Fortschritt, wird ggf. durch Sprünge überschrieben

            String opcode = instr.getOpcode();
            String arg = instr.getOperand();

            //System.out.println("→ Instruktion #" + (instructionPointer - 1) + ": " + instr);

            switch (opcode) {
                case InstructionSet.PUSH:
                    stack.push(Integer.parseInt(arg));
                    break;

                case InstructionSet.GOTO:
                    instructionPointer = labelMap.get(arg);
                    //System.out.println("   [GOTO] → springe zu Label " + arg);
                    break;

                case InstructionSet.LOAD:
                    if (!memory.containsKey(arg)) {
                        throw new RuntimeException("Variable nicht definiert: " + arg);
                    }
                    stack.push(memory.get(arg));
                    break;

                case InstructionSet.STORE:
                    int value = stack.pop();
                    memory.put(arg, value);
                    // 🔵 Debug: Speichervorgang
                    //System.out.println("   [STORE] " + arg + " = " + value);
                    break;

                case InstructionSet.ADD:
                    stack.push(stack.pop() + stack.pop());
                    break;

                case InstructionSet.SUB:
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a - b);
                    break;

                case InstructionSet.MUL:
                    stack.push(stack.pop() * stack.pop());
                    break;

                case InstructionSet.DIV:
                    int divisor = stack.pop();
                    int dividend = stack.pop();
                    if (divisor == 0) throw new ArithmeticException("Division durch 0!");
                    stack.push(dividend / divisor);
                    break;

                case InstructionSet.GT:
                    stack.push(stack.pop() < stack.pop() ? 1 : 0);
                    break;

                case InstructionSet.LT:
                    stack.push(stack.pop() > stack.pop() ? 1 : 0);
                    break;

                case InstructionSet.EQ:
                    stack.push(stack.pop().equals(stack.pop()) ? 1 : 0);
                    break;

                case InstructionSet.NEQ:
                    stack.push(!stack.pop().equals(stack.pop()) ? 1 : 0);
                    break;

                case InstructionSet.GTE:
                    int rhsGTE = stack.pop();
                    int lhsGTE = stack.pop();
                    stack.push(lhsGTE >= rhsGTE ? 1 : 0);
                    break;

                case InstructionSet.LTE:
                    int rhsLTE = stack.pop();
                    int lhsLTE = stack.pop();
                    stack.push(lhsLTE <= rhsLTE ? 1 : 0);
                    break;

                case InstructionSet.JMP:
                    instructionPointer = labelMap.get(arg);
                    //System.out.println("   [JMP] → springe zu Label " + arg);
                    break;

                case InstructionSet.JZ:
                    int condition = stack.pop();
                    if (condition == 0) {
                        instructionPointer = labelMap.get(arg);
                        //System.out.println("   [JZ] Bedingung ist 0 → springe zu " + arg);
                    } else {
                        //System.out.println("   [JZ] Bedingung ist NICHT 0 → kein Sprung");
                    }
                    break;

                case InstructionSet.CALL:
                    callStack.push(new ExecutionContext(instructionPointer));
                    instructionPointer = labelMap.get("FUNC_" + arg);
                    //System.out.println("   [CALL] → Funktionssprung zu FUNC_" + arg + ":");
                    break;

                case InstructionSet.RET:
                    ExecutionContext context = callStack.pop();
                    instructionPointer = context.getReturnAddress();
                    //System.out.println("   [RET] → Rücksprung zu #" + instructionPointer);
                    break;

                default:
                    if (!opcode.endsWith(":")) {
                        throw new RuntimeException("Unbekannter Befehl: " + opcode);
                    }
                    // Labels werden nicht ausgeführt, daher keine Aktion
                    break;
            }

            // 🟢 Debug: Stack nach jedem Schritt
            //System.out.println("   [STACK] " + stack + "\n");
        }

        // 🏁 Endausgabe
        System.out.println("=== Ausführung beendet ===");
        System.out.println("Finaler Stack: " + stack);
        System.out.println("Finaler Speicher: " + memory);
    }
}
