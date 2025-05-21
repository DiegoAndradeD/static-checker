package br.ucsal.compiladores.symbolTable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import br.ucsal.compiladores.config.Constants;

public class SymbolTable {

    private final Map<String, Symbol> table;
    private int nextIndex;

    public SymbolTable() {
        this.table = new LinkedHashMap<>();
        this.nextIndex = 0;
    }

    public int addSymbol(String lexeme, int line) {
        String truncated = truncate(lexeme, Constants.MAX_LEXEME_LENGTH);

        if (table.containsKey(truncated)) {
            Symbol existing = table.get(truncated);
            existing.addAppearanceLine(line);
            return existing.getIndex();
        }

        Symbol symbol = new Symbol(nextIndex, lexeme, Constants.MAX_LEXEME_LENGTH, line);
        table.put(truncated, symbol);
        return nextIndex++;
    }

    public boolean contains(String lexeme) {
        String truncated = truncate(lexeme, Constants.MAX_LEXEME_LENGTH);
        return table.containsKey(truncated);
    }

    public Symbol getSymbol(String lexeme) {
        String truncated = truncate(lexeme, Constants.MAX_LEXEME_LENGTH);
        return table.get(truncated);
    }

    public Collection<Symbol> getAllSymbols() {
        return table.values();
    }

    public int size() {
        return table.size();
    }

    private String truncate(String lexeme, int maxLength) {
        return lexeme.length() <= maxLength ? lexeme : lexeme.substring(0, maxLength);
    }

    public String generateTabReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("TABELA DE SÍMBOLOS\n");
        sb.append(String.format("%-5s %-20s %-8s %-8s %-8s %-15s\n",
                "ID", "Lexema", "OrigLen", "TruncLen", "Tipo", "Linhas"));

        for (Symbol symbol : getAllSymbols()) {
            sb.append(String.format("%-5d %-20s %-8d %-8d %-8s %s\n",
                    symbol.getIndex(),
                    symbol.getTruncatedLexeme(),
                    symbol.getOriginalLength(),
                    symbol.getTruncatedLength(),
                    symbol.getType() != null ? symbol.getType() : "undef",
                    symbol.getAppearanceLines().toString()));
        }

        return sb.toString();
    }
}
