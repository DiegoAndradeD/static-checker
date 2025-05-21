package br.ucsal.compiladores.lexer;

public class Token {
    private String lexeme;
    private TokenType type;
    private int line;
    private int column;
    private Integer symbolIndex;

    public Token(String lexeme, TokenType type, int line, int column) {
        this.lexeme = lexeme;
        this.type = type;
        this.line = line;
        this.column = column;
        this.symbolIndex = null;
    }

    public String getLexeme() {
        return lexeme;
    }

    public TokenType getType() {
        return type;
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

    public void setSymbolIndex(Integer symbolIndex) {
        this.symbolIndex = symbolIndex;
    }

    @Override
    public String toString() {
        return String.format(
                "Token[type=%s, lexeme='%s', line=%d, column=%d%s]",
                type,
                lexeme,
                line,
                column,
                (symbolIndex != null ? ", symbolIndex=" + symbolIndex : ""));
    }
}
