package br.ucsal.compiladores.reserved;

import java.util.HashMap;
import java.util.Map;

import br.ucsal.compiladores.lexer.TokenType;

public class ReservedTable {

    private static final Map<String, TokenType> reservedMap = new HashMap<>();

    static {
        reservedMap.put("program", TokenType.PROGRAM);
        reservedMap.put("declarations", TokenType.DECLARATIONS);
        reservedMap.put("endDeclarations", TokenType.END_DECLARATIONS);
        reservedMap.put("functions", TokenType.FUNCTIONS);
        reservedMap.put("endFunctions", TokenType.END_FUNCTIONS);
        reservedMap.put("endProgram", TokenType.END_PROGRAM);
        reservedMap.put("varType", TokenType.VAR_TYPE);
        reservedMap.put("funcType", TokenType.FUNC_TYPE);
        reservedMap.put("endFunction", TokenType.END_FUNCTION);
        reservedMap.put("paramType", TokenType.PARAM_TYPE);

        reservedMap.put("int", TokenType.INT);
        reservedMap.put("real", TokenType.REAL);
        reservedMap.put("integer", TokenType.INTEGER);
        reservedMap.put("string", TokenType.STRING);
        reservedMap.put("boolean", TokenType.BOOLEAN);
        reservedMap.put("character", TokenType.CHARACTER);
        reservedMap.put("void", TokenType.VOID);

        reservedMap.put("if", TokenType.IF);
        reservedMap.put("endIf", TokenType.END_IF);
        reservedMap.put("else", TokenType.ELSE);
        reservedMap.put("while", TokenType.WHILE);
        reservedMap.put("endWhile", TokenType.END_WHILE);
        reservedMap.put("return", TokenType.RETURN);
        reservedMap.put("break", TokenType.BREAK);
        reservedMap.put("print", TokenType.PRINT);

        reservedMap.put("true", TokenType.TRUE);
        reservedMap.put("false", TokenType.FALSE);

        reservedMap.put(":=", TokenType.ASSIGN);
        reservedMap.put("<=", TokenType.LESS_EQUAL);
        reservedMap.put("<", TokenType.LESS);
        reservedMap.put(">", TokenType.GREATER);
        reservedMap.put(">=", TokenType.GREATER_EQUAL);
        reservedMap.put("==", TokenType.EQUALS);
        reservedMap.put("!=", TokenType.NOT_EQUALS);
        reservedMap.put("#", TokenType.NOT);
        reservedMap.put("-", TokenType.MINUS);
        reservedMap.put("+", TokenType.PLUS);
        reservedMap.put("*", TokenType.MULTIPLY);
        reservedMap.put("/", TokenType.DIVIDE);
        reservedMap.put("%", TokenType.MODULO);

        reservedMap.put(";", TokenType.SEMICOLON);
        reservedMap.put(":", TokenType.COLON);
        reservedMap.put(",", TokenType.COMMA);
        reservedMap.put("(", TokenType.LEFT_PAREN);
        reservedMap.put(")", TokenType.RIGHT_PAREN);
        reservedMap.put("[", TokenType.LEFT_BRACKET);
        reservedMap.put("]", TokenType.RIGHT_BRACKET);
        reservedMap.put("{", TokenType.LEFT_BRACE);
        reservedMap.put("}", TokenType.RIGHT_BRACE);
        reservedMap.put("?", TokenType.QUESTION);
        reservedMap.put(".", TokenType.DOT);
    }

    public static boolean isReserved(String lexeme) {
        return reservedMap.containsKey(lexeme);
    }

    public static TokenType getTokenType(String lexeme) {
        return reservedMap.getOrDefault(lexeme, TokenType.UNKNOWN);
    }
}
