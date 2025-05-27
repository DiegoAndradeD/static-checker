package br.ucsal.compiladores.symbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.ucsal.compiladores.lexer.TokenType;

public class Symbol {
    private String displayLexeme;
    private TokenType tokenType;
    private String category;
    private String dataType;

    private int originalLength;

    private final List<Integer> lineAppearances;
    private static final int MAX_LINE_APPEARANCES = 5;

    public Symbol(String displayLexeme, TokenType tokenType, int originalLength, int line) {
        this.displayLexeme = displayLexeme;
        this.tokenType = tokenType;
        this.originalLength = originalLength;
        this.lineAppearances = new ArrayList<>();
        addAppearance(line);
    }

    public void addAppearance(int lineNumber) {
        if (lineAppearances.size() < MAX_LINE_APPEARANCES) {
            lineAppearances.add(lineNumber);
        }
    }

    public String getLexeme() {
        return displayLexeme;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getOriginalLength() {
        return originalLength;
    }

    public int getStoredLength() {
        return displayLexeme.length();
    }

    public List<Integer> getLineAppearances() {
        return lineAppearances;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(displayLexeme, symbol.displayLexeme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayLexeme);
    }

    @Override
    public String toString() {
        return "Lexeme: " + displayLexeme +
                ", Codigo: " + tokenType.name() +
                ", QtdCharAntesTrunc: " + originalLength +
                ", QtdCharDepoisTrunc: " + getStoredLength() +
                ", TipoSimb: " + (dataType != null ? dataType : "N/D") +
                ", Linhas: " + lineAppearances.toString();
    }
}
