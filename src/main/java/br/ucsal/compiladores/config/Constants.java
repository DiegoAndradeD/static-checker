package br.ucsal.compiladores.config;

import java.util.Set;

import br.ucsal.compiladores.utils.FileHandler;

public class Constants {
    public static final String TEAM_ID = "EQ08";
    public static final String[] TEAM_MEMBERS = {
            "Gabriel Da Silva Azevedo; gabrielsilva.azevedo@ucsal.edu.br; +55 71 9109-7114",
            "Denilson Xavier Oliveira; denilson.oliveira@ucsal.edu.br; +55 75 9902-5074",
            "Joao Victor Aziz Lima; joaovictor.santana@ucsal.edu.br; +55 71 9676-4583",
            "Diego Andrade Deiró; diego.deiro@ucsal.edu.br; +55 71 8328-3791"
    };

    public static final String PROJECT_NAME = "CangaCode2025-1";
    public static final String FILE_EXTENSION = ".251";
    public static final String LEXICAL_REPORT_EXTENSION = ".LEX";
    public static final String SYMBOL_TABLE_REPORT_EXTENSION = ".TAB";

    public static final int MAX_ATOM_EFFECTIVE_LENGTH = 32;
    public static final int MAX_SYMBOL_LEXEME_STORAGE_LENGTH = 35;

    public static final Set<Character> DELIMITERS_OR_TOKEN_STARTERS = Set.of(
            ' ', '\t', '\n', '\r', FileHandler.END_OF_FILE_CHAR,
            ';', ',', ':', '(', ')', '[', ']', '{', '}',
            '+', '-', '*', '/', '%', '?', '=', '!', '#', '<', '>',
            '"', '\'');

    public static final String APP_ROOT_DIRECTORY = ".";
}
