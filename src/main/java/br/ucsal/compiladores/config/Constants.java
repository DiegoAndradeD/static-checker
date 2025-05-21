package br.ucsal.compiladores.config;

public class Constants {

    public static final String PROJECT_NAME = "CangaCode2025-1";
    public static final String FILE_EXTENSION = ".251";

    public static final int MAX_LEXEME_LENGTH = 32;

    public static final char COMMENT_LINE_START = '/';
    public static final String COMMENT_BLOCK_START = "/*";
    public static final String COMMENT_BLOCK_END = "*/";

    public static final char SPACE = ' ';
    public static final char TAB = '\t';
    public static final char NEWLINE = '\n';

    public static final String LEX_REPORT_EXTENSION = ".LEX";
    public static final String SYMBOL_TABLE_REPORT_EXTENSION = ".TAB";

    public static final String TEAM_HEADER = "Equipe: Nome1, Nome2, Nome3";
    public static final String REPORT_SEPARATOR = "-------------------------------";

    public static final String UNKNOWN_TOKEN = "UNKNOWN";
    public static final String RESERVED_CATEGORY = "RESERVED";

    public static final String ERROR_TRUNCATION = "Identificador ou constante truncado.";
    public static final String ERROR_INVALID_CHARACTER = "Caractere inválido ignorado.";

    public static final String IDENTIFIER_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_]*$";

}
