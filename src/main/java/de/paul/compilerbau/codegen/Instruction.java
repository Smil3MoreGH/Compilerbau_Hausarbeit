package de.paul.compilerbau.codegen;

public class Instruction {
    private final String opcode;  // z.B. "PUSH", "LOAD", "ADD"
    private final String operand; // Optionaler Operand (z.B. "5", "x", "label1")

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
