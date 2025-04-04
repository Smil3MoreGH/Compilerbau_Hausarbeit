package de.paul.compilerbau.codegen;

/**
 * Repräsentiert eine einzelne Instruktion im generierten Code.
 * Beispiel: "PUSH 5", "LOAD x", "ADD"
 */
public class Instruction {
    private final String opcode;   // Operationscode, z.B. "PUSH", "LOAD", "ADD"
    private final String operand;  // Optionaler Operand (z.B. "5", "x", "label")

    public Instruction(String opcode) {
        this(opcode, null);
    }

    public Instruction(String opcode, String operand) {
        this.opcode = opcode;
        this.operand = operand;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return operand == null ? opcode : opcode + " " + operand;
    }
}
