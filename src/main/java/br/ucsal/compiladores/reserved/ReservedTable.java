package br.ucsal.compiladores.reserved;

import java.util.HashMap;
import java.util.Map;

import br.ucsal.compiladores.lexer.TokenType;

public class ReservedTable {
    private final Map<String, TokenType> reservedWords;

    public ReservedTable() {
        reservedWords = new HashMap<>();

        reservedWords.put("PROGRAM", TokenType.PRS_PROGRAM);
        reservedWords.put("ENDPROGRAM", TokenType.PRS_END_PROGRAM);
        reservedWords.put("DECLARATIONS", TokenType.PRS_DECLARATIONS);
        reservedWords.put("ENDDECLARATIONS", TokenType.PRS_END_DECLARATIONS);
        reservedWords.put("FUNCTIONS", TokenType.PRS_FUNCTIONS);
        reservedWords.put("ENDFUNCTIONS", TokenType.PRS_END_FUNCTIONS);
        reservedWords.put("VARTYPE", TokenType.PRS_VAR_TYPE);
        reservedWords.put("FUNCTYPE", TokenType.PRS_FUNC_TYPE);
        reservedWords.put("PARAMTYPE", TokenType.PRS_PARAM_TYPE);
        reservedWords.put("INTEGER", TokenType.PRS_INTEGER);
        reservedWords.put("REAL", TokenType.PRS_REAL);
        reservedWords.put("STRING", TokenType.PRS_STRING);
        reservedWords.put("BOOLEAN", TokenType.PRS_BOOLEAN);
        reservedWords.put("CHARACTER", TokenType.PRS_CHARACTER);
        reservedWords.put("VOID", TokenType.PRS_VOID);
        reservedWords.put("TRUE", TokenType.PRS_TRUE);
        reservedWords.put("FALSE", TokenType.PRS_FALSE);
        reservedWords.put("IF", TokenType.PRS_IF);
        reservedWords.put("ELSE", TokenType.PRS_ELSE);
        reservedWords.put("ENDIF", TokenType.PRS_ENDIF);
        reservedWords.put("WHILE", TokenType.PRS_WHILE);
        reservedWords.put("ENDWHILE", TokenType.PRS_ENDWHILE);
        reservedWords.put("RETURN", TokenType.PRS_RETURN);
        reservedWords.put("BREAK", TokenType.PRS_BREAK);
        reservedWords.put("PRINT", TokenType.PRS_PRINT);
        reservedWords.put("ENDFUNCTION", TokenType.PRS_ENDFUNCTION);
    }

    public TokenType getTokenType(String lexeme) {
        return reservedWords.get(lexeme.toUpperCase());
    }

    public boolean isReserved(String lexeme) {
        return reservedWords.containsKey(lexeme.toUpperCase());
    }
}
