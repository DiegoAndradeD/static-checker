package br.ucsal.compiladores.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import br.ucsal.compiladores.config.Constants;
import br.ucsal.compiladores.lexer.Token;
import br.ucsal.compiladores.lexer.TokenType;
import br.ucsal.compiladores.symbolTable.Symbol;
import br.ucsal.compiladores.symbolTable.SymbolTable;

public class ReportGenerator {
    private final SymbolTable symbolTable;
    private final List<Token> tokens;
    private final String inputFilePath;
    private final String baseOutputName;

    public ReportGenerator(SymbolTable symbolTable, List<Token> tokens, String inputFilePath) {
        this.symbolTable = symbolTable;
        this.tokens = tokens;
        this.inputFilePath = inputFilePath;
        Path path = Paths.get(this.inputFilePath);
        String fileNameWithExt = path.getFileName().toString();
        this.baseOutputName = fileNameWithExt.contains(".")
                ? fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf('.'))
                : fileNameWithExt;
    }

    private String getHeader(String reportTitle) {
        StringBuilder header = new StringBuilder();
        header.append("Codigo da Equipe: ").append(Constants.TEAM_ID).append("\n");
        header.append("Componentes:\n");
        for (String member : Constants.TEAM_MEMBERS) {
            header.append("  ").append(member).append("\n");
        }
        header.append("\n").append(reportTitle).append("\n");
        header.append("Texto fonte analisado: ").append(Paths.get(this.inputFilePath).getFileName().toString())
                .append("\n\n");
        return header.toString();
    }

    public void generateLexReport() throws IOException {
        String lexFileName = baseOutputName + Constants.LEXICAL_REPORT_EXTENSION;
        Path inputDir = Paths.get(inputFilePath).getParent();
        Path outputPath = (inputDir != null) ? inputDir.resolve(lexFileName) : Paths.get(lexFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write(getHeader("RELATÓRIO DA ANÁLISE LÉXICA"));

            for (Token token : tokens) {
                if (token.getType() == TokenType.END_OF_FILE)
                    continue;

                writer.write("Lexeme: " + token.getLexeme() + ", ");
                writer.write("Código: " + token.getType().getCode() + ", ");
                if (token.getSymbolIndex() != null) {
                    writer.write("ÍndiceTabSimb: " + token.getSymbolIndex() + ", ");
                } else {
                    writer.write("ÍndiceTabSimb: N/A, ");
                }
                writer.write("Linha: " + token.getLine() + ".\n");
            }
            System.out.println("Relatório LEX gerado em: " + outputPath.toAbsolutePath());
        }
    }

    public void generateTabReport() throws IOException {
        String tabFileName = baseOutputName + Constants.SYMBOL_TABLE_REPORT_EXTENSION;
        Path inputDir = Paths.get(inputFilePath).getParent();
        Path outputPath = (inputDir != null) ? inputDir.resolve(tabFileName) : Paths.get(tabFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write(getHeader("RELATÓRIO DA TABELA DE SÍMBOLOS"));

            List<Symbol> symbols = symbolTable.getAllSymbols();
            for (int i = 0; i < symbols.size(); i++) {
                Symbol symbol = symbols.get(i);
                String tipoSimbDisplay = "N/D";
                if (symbol.getDataType() != null && !symbol.getDataType().isEmpty()
                        && !symbol.getDataType().equals("N/D")) {
                    tipoSimbDisplay = symbol.getDataType();
                } else if (symbol.getCategory() != null && !symbol.getCategory().isEmpty()) {
                    tipoSimbDisplay = symbol.getCategory();
                } else if (symbol.getTokenType() == TokenType.IDN_VARIABLE) {
                    tipoSimbDisplay = "variable";
                }

                writer.write("Entrada: " + (i + 1) + ", ");
                writer.write("Codigo: " + symbol.getTokenType().getCode() + ", ");
                writer.write("Lexeme: " + symbol.getLexeme() + ", \n");
                writer.write("  QtdCharAntesTrunc: " + symbol.getOriginalLength() + ", ");
                writer.write("QtdCharDepoisTrunc: " + symbol.getStoredLength() + ", \n");
                writer.write("  TipoSimb: " + tipoSimbDisplay + ", ");

                List<Integer> lineAppearances = symbol.getLineAppearances();
                String linesStr = lineAppearances.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));

                if (lineAppearances.size() == 1) {
                    writer.write("Linhas: {" + linesStr + "}.\n\n");
                } else {
                    writer.write("Linhas: (" + linesStr + ").\n\n");
                }
            }
            System.out.println("Relatório TAB gerado em: " + outputPath.toAbsolutePath());
        }
    }
}
