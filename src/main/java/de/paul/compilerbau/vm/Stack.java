package de.paul.compilerbau.vm;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Eigene Stack-Implementierung für die VM zur besseren Kontrolle und Fehlermeldung.
 */
public class Stack {
    private final Deque<Integer> stack = new ArrayDeque<>();

    public void push(int value) {
        stack.push(value);
    }

    public int pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack underflow: Versuch, von leerem Stack zu lesen!");
        }
        return stack.pop();
    }

    public int peek() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty: Kein Wert zum Anzeigen!");
        }
        return stack.peek();
    }

    public void clear() {
        stack.clear();
    }

    public int size() {
        return stack.size();
    }

    @Override
    public String toString() {
        return "Stack: " + stack.toString();
    }
}
