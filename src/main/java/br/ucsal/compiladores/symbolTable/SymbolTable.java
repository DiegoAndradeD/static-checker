package br.ucsal.compiladores.symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ucsal.compiladores.lexer.TokenType;

public class SymbolTable {
    private final List<Symbol> symbolsList;
    private final Map<String, Symbol> symbolsMap;

    public SymbolTable() {
        this.symbolsList = new ArrayList<>();
        this.symbolsMap = new HashMap<>();
    }

    public int addOrGetSymbol(String effectiveKeyLexeme, String displayLexeme, TokenType tokenType, int originalLength,
            int lineNumber) {
        if (symbolsMap.containsKey(effectiveKeyLexeme)) {
            Symbol existingSymbol = symbolsMap.get(effectiveKeyLexeme);
            existingSymbol.addAppearance(lineNumber);
            return symbolsList.indexOf(existingSymbol) + 1;
        } else {
            Symbol newSymbol = new Symbol(displayLexeme, tokenType, originalLength, lineNumber);
            symbolsList.add(newSymbol);
            symbolsMap.put(effectiveKeyLexeme, newSymbol);
            return symbolsList.size();
        }
    }

    public Symbol getSymbol(String effectiveKeyLexeme) {
        return symbolsMap.get(effectiveKeyLexeme);
    }

    public Symbol getSymbolByIndex(int indexBase1) {
        if (indexBase1 > 0 && indexBase1 <= symbolsList.size()) {
            return symbolsList.get(indexBase1 - 1);
        }
        return null;
    }

    public List<Symbol> getAllSymbols() {
        return new ArrayList<>(symbolsList);
    }
}
