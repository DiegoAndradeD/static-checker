package br.ucsal.compiladores.symbolTable;

import java.util.ArrayList;
import java.util.List;

public class Symbol {

    private int index;
    private String originalLexeme;
    private String truncatedLexeme;
    private int originalLength;
    private int truncatedLength;
    private String type;
    private List<Integer> appearanceLines;

    public Symbol(int index, String lexeme, int maxLength, int line) {
        this.index = index;
        this.originalLexeme = lexeme;
        this.originalLength = lexeme.length();
        this.truncatedLexeme = truncate(lexeme, maxLength);
        this.truncatedLength = this.truncatedLexeme.length();
        this.type = null;
        this.appearanceLines = new ArrayList<>();
        addAppearanceLine(line);
    }

    private String truncate(String lexeme, int maxLength) {
        return lexeme.length() <= maxLength ? lexeme : lexeme.substring(0, maxLength);
    }

    public void addAppearanceLine(int line) {
        if (appearanceLines.size() < 5 && !appearanceLines.contains(line)) {
            appearanceLines.add(line);
        }
    }

    public int getIndex() {
        return index;
    }

    public String getOriginalLexeme() {
        return originalLexeme;
    }

    public String getTruncatedLexeme() {
        return truncatedLexeme;
    }

    public int getOriginalLength() {
        return originalLength;
    }

    public int getTruncatedLength() {
        return truncatedLength;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getAppearanceLines() {
        return appearanceLines;
    }

    @Override
    public String toString() {
        return String.format(
                "Symbol[%d] lexeme='%s' (orig: %d chars, trunc: %d chars), type='%s', lines=%s",
                index, truncatedLexeme, originalLength, truncatedLength,
                (type != null ? type : "undefined"),
                appearanceLines.toString());
    }
}
