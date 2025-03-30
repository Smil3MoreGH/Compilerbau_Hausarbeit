# PAUL (Parsing And Understanding Language)

Die Programmiersprache **PAUL** (**P**arsing **A**nd **U**nderstanding **L**anguage) ist eine selbst entwickelte funktionale Programmiersprache, die im Rahmen der Hausarbeit im Modul Compilerbau realisiert wird. Sie basiert auf einer kontextfreien Grammatik und wird mit einem rekursiven Abstieg-Parser verarbeitet. Ziel der Sprache ist es, ein tiefgehendes Verständnis für Compilerbau, lexikalische Analyse, syntaktische Analyse sowie Codegenerierung zu erlangen.

## 📜 Kontextfreie Grammatik (CFG) für PAUL

### **🔹 Programmstruktur**
```plaintext
<Programm> ::= <Anweisung> <ProgrammTail>

<ProgrammTail> ::= <Anweisung> <ProgrammTail> | ε
```
➡ Ein **Programm** besteht aus einer **Liste von Anweisungen**.

### **🔹 Anweisungen**
```plaintext
<Anweisung> ::= <Zuweisung> ";"  
              | <FunktionsDefinition>  
              | <FunktionsAufruf> ";"  
              | <Kontrollstruktur>  
              | <Ausdruck> ";"  
              | "return" <Ausdruck> ";"
```
➡ Eine **Anweisung** kann eine **Zuweisung, Funktionsdefinition, Kontrollstruktur oder einen Ausdruck** enthalten.

### **🔹 Zuweisungen**
```plaintext
<Zuweisung> ::= "var" <Identifikator> "=" <Ausdruck>  
              | <Identifikator> "=" <Ausdruck>
```
➡ Variablen können **deklariert und zugewiesen** werden.

### **🔹 Funktionen**
```plaintext
<FunktionsDefinition> ::= "fun" <Identifikator> "(" <ParameterListe> ")" "{" <Programm> "}"

<FunktionsAufruf> ::= <Identifikator> "(" <ArgumentListe> ")"
```
➡ PAUL unterstützt **Funktionen mit Parametern** und **Funktionsaufrufe**.

### **🔹 Parameter und Argumente**
```plaintext
<ParameterListe> ::= <Identifikator> <ParameterListeTail>

<ParameterListeTail> ::= "," <Identifikator> <ParameterListeTail> | ε

<ArgumentListe> ::= <Ausdruck> <ArgumentListeTail>

<ArgumentListeTail> ::= "," <Ausdruck> <ArgumentListeTail> | ε
```
➡ Eine **Funktion** kann **beliebig viele Parameter und Argumente** haben.

### **🔹 Kontrollstrukturen**
```plaintext
<Kontrollstruktur> ::= "if" "(" <Bedingung> ")" "{" <Programm> "}" <ElseBlock>  
                     | "while" "(" <Bedingung> ")" "{" <Programm> "}"

<ElseBlock> ::= "else" "{" <Programm> "}" | ε
```
➡ **Bedingte Anweisungen (`if-else`) und Schleifen (`while`)** sind erlaubt.

### **🔹 Bedingungen und Vergleichsoperatoren**
```plaintext
<Bedingung> ::= <Ausdruck> <Vergleichsoperator> <Ausdruck>

<Vergleichsoperator> ::= "==" | "!=" | "<" | ">" | "<=" | ">="
```
➡ Bedingungen bestehen aus zwei **Ausdrücken** und einem **Vergleichsoperator**.

### **🔹 Arithmetische Operationen**
```plaintext
<Ausdruck> ::= <Term> <AusdruckTail>

<AusdruckTail> ::= "+" <Term> <AusdruckTail>  
                 | "-" <Term> <AusdruckTail>  
                 | ε
```
➡ Ein **Ausdruck** kann eine **Addition oder Subtraktion** enthalten.

### **🔹 Multiplikation und Division**
```plaintext
<Term> ::= <Faktor> <TermTail>

<TermTail> ::= "*" <Faktor> <TermTail>  
             | "/" <Faktor> <TermTail>  
             | ε
```
➡ **Multiplikation und Division** werden hier definiert.

### **🔹 Zahlen, Variablen und Funktionsaufrufe**
```plaintext
<Faktor> ::= <Identifikator>  
           | <Zahl>  
           | "(" <Ausdruck> ")"  
           | <FunktionsAufruf>

<Identifikator> ::= [a-zA-Z_][a-zA-Z0-9_]*

<Zahl> ::= [0-9]+
```
➡ Ein **Faktor** kann eine **Variable, Zahl, Klammer-Ausdruck oder einen Funktionsaufruf** sein.

### **🔹 Epsilon (leeres Wort)**
```plaintext
<Epsilon> ::= ε
```
➡ Epsilon repräsentiert **optionale Regeln oder leere Werte**.
