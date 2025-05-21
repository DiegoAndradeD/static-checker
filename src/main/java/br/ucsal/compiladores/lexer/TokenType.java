package br.ucsal.compiladores.lexer;

public enum TokenType {
    IDENTIFIER,
    INTEGER_CONSTANT,
    FLOAT_CONSTANT,
    STRING_LITERAL,
    CHAR_LITERAL,

    IF,
    ELSE,
    WHILE,
    FOR,
    RETURN,
    INT,
    FLOAT,
    CHAR,
    STRING,
    BOOL,
    TRUE,
    FALSE,
    VOID,
    REAL,
    INTEGER,
    BOOLEAN,
    CHARACTER,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,

    EQUALS,
    NOT_EQUALS,
    GREATER,
    LESS,
    GREATER_EQUAL,
    LESS_EQUAL,

    AND,
    OR,
    NOT,

    ASSIGN,

    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    SEMICOLON,
    COMMA,
    DOT,

    LINE_COMMENT,
    BLOCK_COMMENT_START,
    BLOCK_COMMENT_END,

    EOF,

    UNKNOWN,

    PROGRAM,
    DECLARATIONS,
    END_DECLARATIONS,
    FUNCTIONS,
    END_FUNCTIONS,
    END_PROGRAM,
    VAR_TYPE,
    FUNC_TYPE,
    END_FUNCTION,
    PARAM_TYPE,
    END_IF,
    END_WHILE,
    BREAK,
    PRINT,
    QUESTION,

    COLON,

}
