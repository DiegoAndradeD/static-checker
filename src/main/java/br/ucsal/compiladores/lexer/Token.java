package br.ucsal.compiladores.lexer;

public class Token {
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;
    private final Integer symbolIndex;

    public Token(TokenType type, String lexeme, int line, int column, Integer symbolIndex) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.symbolIndex = symbolIndex;
    }

    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, line, column, null);
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Integer getSymbolIndex() {
        return symbolIndex;
    }

    @Override
    public String toString() {
        String symbolIdxStr = (symbolIndex != null) ? ", IndexTabSimb: " + symbolIndex : "";
        return String.format("Lexeme: %s, Código: %s%s, Linha: %d, Coluna: %d",
                lexeme, type.name(), symbolIdxStr, line, column);
    }
}
