package br.ucsal.compiladores.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import br.ucsal.compiladores.config.Constants;

public class FileHandler {
    private final String filePath;
    private final List<String> lines;
    private int currentLineIndex;
    private int currentCharIndexInLine;
    private char currentChar;
    private boolean justCrossedLine;

    public static final char END_OF_FILE_CHAR = '\0';

    public FileHandler(String baseFileName, String directoryPath) throws IOException {
        this.filePath = Paths.get(directoryPath, baseFileName + Constants.FILE_EXTENSION).toString();
        Path path = Paths.get(this.filePath);

        if (!Files.exists(path)) {
            throw new IOException("Arquivo não encontrado: " + this.filePath);
        }

        this.lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath))) {
            String line = reader.readLine();
            while (line != null) {
                this.lines.add(line);
            }
        }

        this.currentLineIndex = 0;
        this.currentCharIndexInLine = -1;
        this.justCrossedLine = false;
        advance();
    }

    public String getFilePath() {
        return filePath;
    }

    public List<String> getAllLines() {
        return new ArrayList<>(lines);
    }

    public char getCurrentChar() {
        return this.currentChar;
    }

    public int getCurrentLineNumber() {
        return this.currentLineIndex + 1;
    }

    public int getCurrentColumnNumber() {
        return this.currentCharIndexInLine + 1;
    }

    public void advance() {
        if (justCrossedLine) {
            currentLineIndex++;
            currentCharIndexInLine = 0;
            justCrossedLine = false;

            if (currentLineIndex >= lines.size()) {
                this.currentChar = END_OF_FILE_CHAR;
                return;
            }
            String currentLineContent = lines.get(currentLineIndex);
            this.currentChar = currentLineContent.isEmpty() ? '\n' : currentLineContent.charAt(currentCharIndexInLine);
            return;
        }

        currentCharIndexInLine++;

        if (currentLineIndex >= lines.size()) {
            this.currentChar = END_OF_FILE_CHAR;
            return;
        }

        String currentLineContent = lines.get(currentLineIndex);

        if (currentCharIndexInLine >= currentLineContent.length()) {
            this.currentChar = '\n';
            this.justCrossedLine = true;
        } else {
            this.currentChar = currentLineContent.charAt(currentCharIndexInLine);
        }
    }

    public char peek() {
        int peekLine = currentLineIndex;
        int peekCol = currentCharIndexInLine;
        boolean simulatingJustCrossedLine = justCrossedLine;

        if (simulatingJustCrossedLine) {
            peekLine++;
            peekCol = 0;

            if (peekLine >= lines.size()) {
                return END_OF_FILE_CHAR;
            }
            String currentLineContent = lines.get(peekLine);
            return currentLineContent.isEmpty() ? '\n' : currentLineContent.charAt(peekCol);
        }

        peekCol++;
        if (peekLine >= lines.size()) {
            return END_OF_FILE_CHAR;
        }

        String currentLineContent = lines.get(peekLine);
        if (peekCol >= currentLineContent.length()) {
            return '\n';
        } else {
            return currentLineContent.charAt(peekCol);
        }
    }

}
