package br.ucsal.compiladores.lexer;

public class Lexeme {
    private String original;
    private String truncated;
    private int line;
    private int column;

    public Lexeme(String original, int line, int column, int maxLength) {
        this.original = original;
        this.truncated = truncate(original, maxLength);
        this.line = line;
        this.column = column;
    }

    private String truncate(String input, int maxLength) {
        return input.length() <= maxLength ? input : input.substring(0, maxLength);
    }

    public String getOriginal() {
        return original;
    }

    public String getTruncated() {
        return truncated;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Lexeme[original='%s', truncated='%s', line=%d, column=%d]",
                original, truncated, line, column);
    }
}
