# ✅ Checkliste für die Hausarbeit Compilerbau

## 🔹 Planung & Definition der Sprache

### 1️⃣ Kontextfreie Grammatik (CFG) für die Sprache definieren
- **Syntax** (Regeln der Sprache)
- **Semantik** (Bedeutung der Konstrukte)
- **Erweiterung vorhandener Sprachelemente aus der Vorlesung**

## 🔹 Compiler-Implementierung (nach rekursivem Abstieg)

### 2️⃣ Scanner (Lexikalische Analyse) entwickeln
- **Eingabe:** Quellcode
- **Ausgabe:** Token-Liste (z. B. `NUMBER`, `VARIABLE`, `OPERATOR`)
- **Methode:** Reguläre Ausdrücke oder State Machine

### 3️⃣ Parser (Syntaktische Analyse) entwickeln
- **Eingabe:** Token-Liste
- **Ausgabe:** Abstrakter Syntaxbaum (AST)
- **Methode:** Rekursiver Abstieg
- **Fehlerbehandlung einbauen** (z. B. unerwartete Tokens)

### 4️⃣ Codeerzeugung (Zwischencode für Kellermaschine) entwickeln
- **Eingabe:** Syntaxbaum (AST)
- **Ausgabe:** Zwischencode (Stack-Maschinen-Code)
- **Methode:** AST-Traversierung + Übersetzung in Stack-Befehle
- **Beispiel-Zwischencode:**  
  ```assembly
  LOAD x
  PUSH 10
  ADD
  STORE y
  ```

### 5️⃣ Abstrakte Kellermaschine entwickeln
- **Eingabe:** Zwischencode
- **Ausführung durch eine Stack-basierte Architektur**
- **Befehle implementieren:** `PUSH`, `POP`, `ADD`, `SUB`, `JUMP`, `CALL`, etc.

## 🔹 Abschluss & Testen

### 6️⃣ Testfälle entwickeln und ausführen
- **Scanner-Test:** Tokenisierung testen
- **Parser-Test:** AST für verschiedene Eingaben prüfen
- **Codeerzeugung-Test:** Zwischencode überprüfen
- **Kellermaschinen-Test:** Programmausführung simulieren

---

## 7️⃣ Dokumentation & Präsentation für das Kolloquium vorbereiten
- **Code-Dokumentation (Kommentare & Architekturübersicht)**
- **Ergebnisse & Herausforderungen beschreiben**
- **Demo des Compilers vorbereiten**
