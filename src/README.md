[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/gRcDEqF4)
# CS3210-Project-02
Develop a recognizer for the BNF grammar specified below.

```
<program> → <statement>*

<statement> → <declaration> 
            | <assignment> 
            | <if-statement> 
            | <while-statement> 
            | <print-statement>

<declaration> → "let" <identifier> "=" <expression> ";"

<assignment> → <identifier> "=" <expression> ";"

<if-statement> → "if" "(" <condition> ")" "{" <statement>* "}"

<while-statement> → "while" "(" <condition> ")" "{" <statement>* "}"

<print-statement> → "print" "(" <expression> ")" ";"

<condition> → <expression> <relational-operator> <expression>

<relational-operator> → "==" | "!=" | "<" | ">" | "<=" | ">="

<expression> → <term> (("+" | "-") <term>)*

<term> → <factor> (("*" | "/") <factor>)*

<factor> → <number> 
         | <identifier> 
         | "(" <expression> ")"

<identifier> → [a-zA-Z_] [a-zA-Z0-9_]*

<number> → [0-9]+

```
TOKEN TYPES (22 types):

KEYWORD_LET   -> "let"
KEYWORD_IF    -> "if"
KEYWORD_WHILE -> "while"
KEYWORD_PRINT -> "print"

PAREN_LEFT    -> "("
PAREN_RIGHT   -> ")"

BRACE_LEFT    -> "{"
BRACE_RIGHT   -> "}"

SEMICOLON     -> ";"

EQUALS        -> "="
PLUS          -> "+"
MINUS         -> "-"
MULTIPLY      -> "*"
DIVIDE        -> "/"

EQUAL_TO      -> "=="
NOT_EQUAL     -> "!="
GREATER       -> ">"
GREATER_EQUAL -> ">="
LESS_THAN     -> "<"
LESS_EQUAL    -> "<="

IDENTIFIER    -> [a-zA-Z_] [a-zA-Z0-9_]
NUMBER        -> [0-9]

```
Where:

**\<program\>**: A program consists of multiple statements.

**\<statement\>**: A statement can be a variable declaration, assignment, if-statement, while-loop, or print statement.

**\<declaration\>**: Defines variables using the let keyword.

**\<assignment\>**: Assigns a new value to an existing variable.

**\<if-statement\>**: Executes a block of code only if the condition is true.

**\<while-statement\>**: Executes a block of code while a condition remains true.

**\<print-statement\>**: Outputs a value to the console.

**\<expression\>**: Supports arithmetic operations.

**\<identifier\>**: Defines variable names such as x, sum, counter.

**<condition>**: A condition is a logical statement that evaluates to true or false.

**<relational-operator>**: Defines comparison operators for conditions.

**\<number\>**: Represents integer values.

**Some code examples validated by this BNF**

```
Example 1:
let x = 5;
if (x > 2) {
    x = x + 1;
}

Example 2:
let counter = 0;
while (counter < 5) {
    counter = counter + 1;
}

Example 3:
let x = 42;
print(x);

Example 4:
let x = 3;
let y = 10;

Example 5:
if (x < y) {
    while (x < y) {
        print(x);
        x = x + 1;
    }
}
```
**Some code examples are not validated by this BNF**

```
Example 1: Invalid – Missing ; after declaration.
let x = 5 

Example 2: Invalid – Missing closing }.
while (x < 10) {
    x = x + 1;

Example 3: Invalid – = is an assignment, should use ==.
if (x = 5) {  // Should be "x == 5"
    print(x);
}
```






