# Ryak

A custom BNF (Backus-Naur Form) parser written in Java.

## Overview

Ryak is a parsing framework designed to process BNF grammar definitions and parse input according to those grammars. 

## Features

- Custom BNF grammar parsing
- Java-based implementation
- Extensible architecture for interpreter development
- Foundation for language processing tools

## Current Status

**Development Phase**: Active development  
**Language**: Java (with planned C++ migration)  
**Functionality**: Parser (interpreter in development)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven or Gradle (if using build tools)

### Installation

```bash
git clone https://github.com/RyePankoski/Ryak.git
cd Ryak
```

### Basic Usage

```java
// Example usage (actual API may vary based on implementation)
BNFParser parser = new BNFParser();
Grammar grammar = parser.loadGrammar("path/to/grammar.bnf");
ParseResult result = parser.parse("input string", grammar);
```

## Grammar Format

Ryak supports standard BNF notation:

```bnf
<expression> ::= <term> | <expression> "+" <term>
<term> ::= <factor> | <term> "*" <factor>  
<factor> ::= "(" <expression> ")" | <number>
<number> ::= [0-9]+
```

## Roadmap

- **Phase 1**: Complete BNF parser implementation ✓
- **Phase 2**: Add interpreter functionality (in progress)
- **Phase 3**: Performance optimization
- **Phase 4**: Potential C++ migration for performance
- **Phase 5**: Extended language features

## Contributing

This is primarily a learning and research project. Issues and suggestions are welcome.

## Technical Notes

### Design Decisions

- **Java Implementation**: Chosen for rapid prototyping and robust ecosystem
- **Custom Parser**: Built from scratch to understand parsing fundamentals
- **Modular Architecture**: Designed to support future interpreter features


## Future Considerations

Potential migration to C++ is being considered.

![ryak logo](https://github.com/user-attachments/assets/5115175d-094b-4f95-80d6-d77c67a3fbe9)
