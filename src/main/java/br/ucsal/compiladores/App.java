package br.ucsal.compiladores;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import br.ucsal.compiladores.config.Constants;
import br.ucsal.compiladores.lexer.Lexer;
import br.ucsal.compiladores.lexer.Token;
import br.ucsal.compiladores.parser.Parser;
import br.ucsal.compiladores.symbolTable.SymbolTable;
import br.ucsal.compiladores.utils.FileHandler;
import br.ucsal.compiladores.utils.ReportGenerator;

public class App {

    private static class FileInputDetails {
        String baseFileName;
        String directoryPath;
        String inputFilePath;

        FileInputDetails(String baseFileName, String directoryPath, String inputFilePath) {
            this.baseFileName = baseFileName;
            this.directoryPath = directoryPath;
            this.inputFilePath = inputFilePath;
        }
    }

    public static void main(String[] args) {
        System.out.println("Static Checker em execução!");
        Scanner scanner = new Scanner(System.in);
        FileInputDetails fileDetails = null;

        while (fileDetails == null) {
            displayMenu();
            int choice = getUserChoice(scanner);

            if (choice == 4) {
                System.out.println("Saindo...");
                scanner.close();
                return;
            }

            fileDetails = getFileDetailsFromUser(choice, scanner);
            if (fileDetails == null && choice != 4) {
                System.err.println("Não foi possível determinar o arquivo. Tente novamente.");
            }
        }

        scanner.close();

        processFile(fileDetails);
    }

    private static void displayMenu() {
        System.out.println("\nEscolha uma opção para o arquivo fonte:");
        System.out.println("1. Usar arquivo de teste padrão ('Teste.251' no diretório atual)");
        System.out.println("2. Informar nome do arquivo (será procurado no diretório atual)");
        System.out.println("3. Informar caminho completo do arquivo");
        System.out.println("4. Sair");
        System.out.print("Opção: ");
    }

    private static int getUserChoice(Scanner scanner) {
        String choiceStr = scanner.nextLine().trim();
        try {
            return Integer.parseInt(choiceStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static FileInputDetails getFileDetailsFromUser(int choice, Scanner scanner) {
        String baseFileName = null;
        String directoryPath = ".";
        String inputFilePath = null;

        switch (choice) {
            case 1:
                baseFileName = "Teste";
                directoryPath = ".";
                inputFilePath = Paths.get(directoryPath, baseFileName + Constants.FILE_EXTENSION).toAbsolutePath()
                        .toString();
                System.out.println("Usando arquivo de teste padrão: " + inputFilePath);
                return new FileInputDetails(baseFileName, directoryPath, inputFilePath);

            case 2:
                System.out.print("Informe o nome base do arquivo (sem a extensão .251): ");
                baseFileName = scanner.nextLine().trim();
                if (baseFileName.isEmpty()) {
                    System.err.println("Nome do arquivo não pode ser vazio.");
                    return null;
                }
                directoryPath = ".";
                inputFilePath = Paths.get(directoryPath, baseFileName + Constants.FILE_EXTENSION).toAbsolutePath()
                        .toString();
                System.out.println("Procurando por: " + inputFilePath);
                return new FileInputDetails(baseFileName, directoryPath, inputFilePath);

            case 3:
                System.out.print("Informe o caminho (completo ou relativo) para o arquivo (extensão .251 opcional): ");
                String userInputPath = scanner.nextLine().trim();
                if (userInputPath.isEmpty()) {
                    System.err.println("Caminho do arquivo não pode ser vazio.");
                    return null;
                }

                Path potentialPath;
                if (userInputPath.toLowerCase().endsWith(Constants.FILE_EXTENSION)) {
                    potentialPath = Paths.get(userInputPath);
                } else {
                    potentialPath = Paths.get(userInputPath + Constants.FILE_EXTENSION);
                }

                Path absolutePathToTest = potentialPath.toAbsolutePath();

                if (!Files.isRegularFile(absolutePathToTest)) {
                    System.err.println(
                            "Erro: Arquivo não encontrado ou não é um arquivo regular em: " + absolutePathToTest);
                    return null;
                }

                inputFilePath = absolutePathToTest.toString();
                String fileNameWithExt = absolutePathToTest.getFileName().toString();

                baseFileName = fileNameWithExt.substring(0, fileNameWithExt.length() - 4);

                Path parentDir = absolutePathToTest.getParent();
                directoryPath = (parentDir != null) ? parentDir.toString() : ".";

                System.out.println("Usando arquivo: " + inputFilePath);
                return new FileInputDetails(baseFileName, directoryPath, inputFilePath);

            case 4:
                return null;

            default:
                System.err.println("Opção inválida. Tente novamente.");
                return null;
        }
    }

    private static void processFile(FileInputDetails fileDetails) {
        if (fileDetails.baseFileName == null || fileDetails.inputFilePath == null) {
            System.err.println("Erro fatal: Detalhes do arquivo não configurados corretamente antes de processar.");
            return;
        }

        try {
            FileHandler fileHandler = new FileHandler(fileDetails.baseFileName, fileDetails.directoryPath);
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
            System.err.println("Erro de E/S: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado durante a análise: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
