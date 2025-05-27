package br.ucsal.compiladores;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import br.ucsal.compiladores.lexer.Lexer;
import br.ucsal.compiladores.lexer.Token;
import br.ucsal.compiladores.parser.Parser;
import br.ucsal.compiladores.symbolTable.SymbolTable;
import br.ucsal.compiladores.utils.FileHandler;
import br.ucsal.compiladores.utils.ReportGenerator;

public class App {
    public static void main(String[] args) {
        System.out.println("Static Checker CangaCode2025-1 Iniciado!");

        String baseFileName = "Teste";
        String directoryPath = ".";
        @SuppressWarnings("unused")
        String inputFilePath;

        Path fullPathArg = Paths.get(baseFileName);
        if (fullPathArg.getParent() != null) {
            inputFilePath = Paths.get(directoryPath, baseFileName + ".251").toAbsolutePath().toString();
            directoryPath = fullPathArg.getParent().toString();
            baseFileName = fullPathArg.getFileName().toString();

        }

        try {
            FileHandler fileHandler = new FileHandler(baseFileName, directoryPath);
            System.out.println("Arquivo a ser processado: " + fileHandler.getFilePath());

            SymbolTable symbolTable = new SymbolTable();
            Lexer lexer = new Lexer(fileHandler, symbolTable);
            List<Token> tokens = lexer.tokenize();

            System.out.println("\nTokens encontrados:");
            for (Token token : tokens) {
                System.out.println(token.toString());
            }

            Parser parser = new Parser(tokens, symbolTable);
            parser.check();

            ReportGenerator reportGenerator = new ReportGenerator(symbolTable, tokens, fileHandler.getFilePath());
            reportGenerator.generateLexReport();
            reportGenerator.generateTabReport();

        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
