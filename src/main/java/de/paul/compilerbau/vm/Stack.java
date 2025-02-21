package de.paul.compilerbau.vm;

import java.util.ArrayDeque;
import java.util.Deque;

public class Stack {
    private final Deque<Integer> stack = new ArrayDeque<>();

    // ✅ Wert auf den Stack legen
    public void push(int value) {
        stack.push(value);
    }

    // ✅ Wert vom Stack holen
    public int pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack underflow: Versuch, von leerem Stack zu lesen!");
        }
        return stack.pop();
    }

    // ✅ Letzter Wert ohne Entfernen ansehen
    public int peek() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty: Kein Wert zum Anzeigen!");
        }
        return stack.peek();
    }

    // ✅ Stack leeren (optional)
    public void clear() {
        stack.clear();
    }

    // ✅ Stackgröße (Debug)
    public int size() {
        return stack.size();
    }

    @Override
    public String toString() {
        return "Stack: " + stack.toString();
    }
}
