package br.ucsal.compiladores.lexer;

import br.ucsal.compiladores.config.Constants;
import br.ucsal.compiladores.reserved.ReservedTable;
import br.ucsal.compiladores.symbolTable.SymbolTable;
import br.ucsal.compiladores.utils.FileHandler;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final FileHandler fileHandler;
    private final ReservedTable reservedTable;
    private final SymbolTable symbolTable;

    public Lexer(FileHandler fileHandler, SymbolTable symbolTable) {
        this.fileHandler = fileHandler;
        this.reservedTable = new ReservedTable();
        this.symbolTable = symbolTable;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = getNextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.END_OF_FILE);
        return tokens;
    }

    private void skipWhitespaceAndComments() {
        while (true) {
            char currentChar = fileHandler.getCurrentChar();
            if (currentChar == FileHandler.END_OF_FILE_CHAR)
                break;
            if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n' || currentChar == '\r') {
                fileHandler.advance();
                continue;
            }
            if (currentChar == '/') {
                char peekChar = fileHandler.peek();
                if (peekChar == '/') {
                    int commentLineStart = fileHandler.getCurrentLineNumber();
                    fileHandler.advance();
                    fileHandler.advance();
                    while (fileHandler.getCurrentChar() != FileHandler.END_OF_FILE_CHAR
                            && fileHandler.getCurrentLineNumber() == commentLineStart) {
                        fileHandler.advance();
                    }
                    continue;
                } else if (peekChar == '*') {
                    fileHandler.advance();
                    fileHandler.advance();
                    while (true) {
                        char insideBlockChar = fileHandler.getCurrentChar();
                        if (insideBlockChar == FileHandler.END_OF_FILE_CHAR)
                            break;
                        if (insideBlockChar == '*' && fileHandler.peek() == '/') {
                            fileHandler.advance();
                            fileHandler.advance();
                            break;
                        }
                        fileHandler.advance();
                    }
                    continue;
                }
            }
            break;
        }
    }

    private boolean isCharToFilter(char c) {
        if (c == FileHandler.END_OF_FILE_CHAR)
            return false;
        if (Character.isLetterOrDigit(c) || c == '_')
            return false;
        if (c == '"' || c == '\'')
            return false;
        if (c == '.')
            return false;
        return !Constants.DELIMITERS_OR_TOKEN_STARTERS.contains(c);
    }

    private Token getNextToken() {
        skipWhitespaceAndComments();

        while (isCharToFilter(fileHandler.getCurrentChar())) {
            fileHandler.advance();
            skipWhitespaceAndComments();
        }

        int tokenLine = fileHandler.getCurrentLineNumber();
        int tokenColumn = fileHandler.getCurrentColumnNumber();
        char currentChar = fileHandler.getCurrentChar();

        if (currentChar == FileHandler.END_OF_FILE_CHAR) {
            return new Token(TokenType.END_OF_FILE, "", tokenLine, tokenColumn);
        }

        Token token;

        token = tryParseIdentifierOrKeyword(tokenLine, tokenColumn, currentChar);
        if (token != null)
            return token;

        token = tryParseNumber(tokenLine, tokenColumn, currentChar);
        if (token != null)
            return token;

        token = tryParseStringLiteral(tokenLine, tokenColumn, currentChar);
        if (token != null)
            return token;

        token = tryParseCharLiteral(tokenLine, tokenColumn, currentChar);
        if (token != null)
            return token;

        return parseSymbolOrUnknown(tokenLine, tokenColumn, fileHandler.getCurrentChar());
    }

    private Token tryParseIdentifierOrKeyword(int tokenLine, int tokenColumn, char currentChar) {
        if (Character.isLetter(currentChar) || currentChar == '_') {
            StringBuilder sb = new StringBuilder();
            @SuppressWarnings("unused")
            int originalLengthCounter = 0;
            char currentLocalChar = currentChar;

            while (true) {
                if (Character.isLetterOrDigit(currentLocalChar) || currentLocalChar == '_') {
                    sb.append(currentLocalChar);
                    fileHandler.advance();
                    currentLocalChar = fileHandler.getCurrentChar();
                    originalLengthCounter++;
                } else if (isCharToFilter(currentLocalChar)) {
                    fileHandler.advance();
                    currentLocalChar = fileHandler.getCurrentChar();
                    originalLengthCounter++;
                } else {
                    break;
                }
            }

            String fullRawLexemeCollected = sb.toString();
            int finalOriginalLength = fullRawLexemeCollected.length();

            String effectiveLexemeForTokenAndKey = fullRawLexemeCollected;
            if (finalOriginalLength > Constants.MAX_ATOM_EFFECTIVE_LENGTH) {
                effectiveLexemeForTokenAndKey = fullRawLexemeCollected.substring(0,
                        Constants.MAX_ATOM_EFFECTIVE_LENGTH);
            }
            String upperEffectiveLexeme = effectiveLexemeForTokenAndKey.toUpperCase();

            String displayLexemeForSymbol = fullRawLexemeCollected;
            if (finalOriginalLength > Constants.MAX_SYMBOL_LEXEME_STORAGE_LENGTH) {
                displayLexemeForSymbol = fullRawLexemeCollected.substring(0,
                        Constants.MAX_SYMBOL_LEXEME_STORAGE_LENGTH);
            }
            String upperDisplayLexeme = displayLexemeForSymbol.toUpperCase();

            TokenType reservedType = reservedTable.getTokenType(upperEffectiveLexeme);
            if (reservedType != null) {
                return new Token(reservedType, upperEffectiveLexeme, tokenLine, tokenColumn);
            } else {
                int symbolIndex = symbolTable.addOrGetSymbol(
                        upperEffectiveLexeme,
                        upperDisplayLexeme,
                        TokenType.IDN_VARIABLE,
                        finalOriginalLength,
                        tokenLine);
                return new Token(TokenType.IDN_VARIABLE, upperEffectiveLexeme, tokenLine, tokenColumn, symbolIndex);
            }
        }
        return null;
    }

    private Token tryParseNumber(int tokenLine, int tokenColumn, char currentChar) {
        if (Character.isDigit(currentChar) || (currentChar == '.' && Character.isDigit(fileHandler.peek()))) {
            StringBuilder sb = new StringBuilder();
            TokenType currentNumberType = TokenType.IDN_INT_CONST;
            char currentLocalChar = currentChar;

            if (currentLocalChar == '.') {
                sb.append(currentLocalChar);
                fileHandler.advance();
                currentLocalChar = fileHandler.getCurrentChar();
                currentNumberType = TokenType.IDN_REAL_CONST;
            }

            while (Character.isDigit(currentLocalChar)) {
                sb.append(currentLocalChar);
                fileHandler.advance();
                currentLocalChar = fileHandler.getCurrentChar();
            }

            if (currentLocalChar == '.' && currentNumberType == TokenType.IDN_INT_CONST
                    && Character.isDigit(fileHandler.peek())) {
                currentNumberType = TokenType.IDN_REAL_CONST;
                sb.append(currentLocalChar);
                fileHandler.advance();
                currentLocalChar = fileHandler.getCurrentChar();
                while (Character.isDigit(currentLocalChar)) {
                    sb.append(currentLocalChar);
                    fileHandler.advance();
                    currentLocalChar = fileHandler.getCurrentChar();
                }
            }

            if (currentNumberType == TokenType.IDN_REAL_CONST && (currentLocalChar == 'e' || currentLocalChar == 'E')) {
                StringBuilder exponentPartAttempt = new StringBuilder();
                exponentPartAttempt.append(currentLocalChar);

                fileHandler.advance();
                char charAfterE = fileHandler.getCurrentChar();

                if (charAfterE == '+' || charAfterE == '-') {
                    exponentPartAttempt.append(charAfterE);
                    fileHandler.advance();
                    charAfterE = fileHandler.getCurrentChar();
                }

                if (Character.isDigit(charAfterE)) {
                    while (Character.isDigit(charAfterE)) {
                        exponentPartAttempt.append(charAfterE);
                        fileHandler.advance();
                        charAfterE = fileHandler.getCurrentChar();
                    }
                    sb.append(exponentPartAttempt.toString());
                }
            }

            String fullNumericLexeme = sb.toString();
            if (fullNumericLexeme.isEmpty()) {
                return null;
            }

            String tokenNumericLexeme = fullNumericLexeme;
            if (fullNumericLexeme.length() > Constants.MAX_ATOM_EFFECTIVE_LENGTH) {
                tokenNumericLexeme = fullNumericLexeme.substring(0, Constants.MAX_ATOM_EFFECTIVE_LENGTH);
            }
            return new Token(currentNumberType, tokenNumericLexeme, tokenLine, tokenColumn);
        }
        return null;
    }

    private Token tryParseStringLiteral(int tokenLine, int tokenColumn, char currentChar) {
        if (currentChar == '"') {
            StringBuilder sb = new StringBuilder();
            sb.append(currentChar);
            fileHandler.advance();
            char currentLocalChar = fileHandler.getCurrentChar();

            while (currentLocalChar != '"' &&
                    currentLocalChar != FileHandler.END_OF_FILE_CHAR &&
                    fileHandler.getCurrentLineNumber() == tokenLine) {
                sb.append(currentLocalChar);
                fileHandler.advance();
                currentLocalChar = fileHandler.getCurrentChar();
            }

            String tokenStringLexeme;
            TokenType type;
            if (currentLocalChar == '"') {
                sb.append(currentLocalChar);
                fileHandler.advance();
                tokenStringLexeme = sb.toString();
                type = TokenType.IDN_STRING_CONST;
            } else {
                tokenStringLexeme = sb.toString();
                type = TokenType.UNKNOWN;
            }

            if (tokenStringLexeme.length() > Constants.MAX_ATOM_EFFECTIVE_LENGTH) {
                tokenStringLexeme = tokenStringLexeme.substring(0, Constants.MAX_ATOM_EFFECTIVE_LENGTH);
            }
            return new Token(type, tokenStringLexeme, tokenLine, tokenColumn);
        }
        return null;
    }

    private Token tryParseCharLiteral(int tokenLine, int tokenColumn, char currentChar) {
        if (currentChar == '\'') {
            StringBuilder sb = new StringBuilder();
            sb.append(currentChar);
            fileHandler.advance();
            char charInside = fileHandler.getCurrentChar();

            if (Character.isLetter(charInside) && fileHandler.getCurrentLineNumber() == tokenLine) {
                sb.append(charInside);
                fileHandler.advance();
                if (fileHandler.getCurrentChar() == '\'' && fileHandler.getCurrentLineNumber() == tokenLine) {
                    sb.append(fileHandler.getCurrentChar());
                    fileHandler.advance();
                    return new Token(TokenType.IDN_CHAR_CONST, sb.toString(), tokenLine, tokenColumn);
                } else {
                    return new Token(TokenType.UNKNOWN, sb.toString(), tokenLine, tokenColumn);
                }
            } else {
                return new Token(TokenType.UNKNOWN, sb.toString(), tokenLine, tokenColumn);
            }
        }
        return null;
    }

    private Token parseSymbolOrUnknown(int tokenLine, int tokenColumn, char currentChar) {
        String symbolLexeme = "";
        TokenType symbolType = TokenType.UNKNOWN;

        switch (currentChar) {
            case ';':
                symbolLexeme = ";";
                symbolType = TokenType.SRS_SEMICOLON;
                fileHandler.advance();
                break;
            case ',':
                symbolLexeme = ",";
                symbolType = TokenType.SRS_COMMA;
                fileHandler.advance();
                break;
            case '(':
                symbolLexeme = "(";
                symbolType = TokenType.SRS_LEFT_PARENTHESIS;
                fileHandler.advance();
                break;
            case ')':
                symbolLexeme = ")";
                symbolType = TokenType.SRS_RIGHT_PARENTHESIS;
                fileHandler.advance();
                break;
            case '[':
                symbolLexeme = "[";
                symbolType = TokenType.SRS_LEFT_BRACKET;
                fileHandler.advance();
                break;
            case ']':
                symbolLexeme = "]";
                symbolType = TokenType.SRS_RIGHT_BRACKET;
                fileHandler.advance();
                break;
            case '{':
                symbolLexeme = "{";
                symbolType = TokenType.SRS_LEFT_BRACE;
                fileHandler.advance();
                break;
            case '}':
                symbolLexeme = "}";
                symbolType = TokenType.SRS_RIGHT_BRACE;
                fileHandler.advance();
                break;
            case '+':
                symbolLexeme = "+";
                symbolType = TokenType.SRS_PLUS;
                fileHandler.advance();
                break;
            case '-':
                symbolLexeme = "-";
                symbolType = TokenType.SRS_MINUS;
                fileHandler.advance();
                break;
            case '*':
                symbolLexeme = "*";
                symbolType = TokenType.SRS_MULTIPLICATION;
                fileHandler.advance();
                break;
            case '/':
                symbolLexeme = "/";
                symbolType = TokenType.SRS_DIVISION;
                fileHandler.advance();
                break;
            case '%':
                symbolLexeme = "%";
                symbolType = TokenType.SRS_MODULO;
                fileHandler.advance();
                break;
            case '?':
                symbolLexeme = "?";
                symbolType = TokenType.SRS_QUESTION_MARK;
                fileHandler.advance();
                break;
            case ':':
                if (fileHandler.peek() == '=') {
                    fileHandler.advance();
                    fileHandler.advance();
                    symbolLexeme = ":=";
                    symbolType = TokenType.SRS_ASSIGNMENT;
                } else {
                    fileHandler.advance();
                    symbolLexeme = ":";
                    symbolType = TokenType.SRS_COLON;
                }
                break;
            case '=':
                if (fileHandler.peek() == '=') {
                    fileHandler.advance();
                    fileHandler.advance();
                    symbolLexeme = "==";
                    symbolType = TokenType.SRS_EQUAL_COMPARISON;
                } else {
                    symbolLexeme = String.valueOf(currentChar).toUpperCase();
                    fileHandler.advance();
                }
                break;
            case '!':
                if (fileHandler.peek() == '=') {
                    fileHandler.advance();
                    fileHandler.advance();
                    symbolLexeme = "!=";
                    symbolType = TokenType.SRS_NOT_EQUAL;
                } else {
                    symbolLexeme = String.valueOf(currentChar).toUpperCase();
                    fileHandler.advance();
                }
                break;
            case '#':
                fileHandler.advance();
                symbolLexeme = "#";
                symbolType = TokenType.SRS_NOT_EQUAL;
                break;
            case '<':
                if (fileHandler.peek() == '=') {
                    fileHandler.advance();
                    fileHandler.advance();
                    symbolLexeme = "<=";
                    symbolType = TokenType.SRS_LESS_THAN_OR_EQUAL;
                } else {
                    fileHandler.advance();
                    symbolLexeme = "<";
                    symbolType = TokenType.SRS_LESS_THAN;
                }
                break;
            case '>':
                if (fileHandler.peek() == '=') {
                    fileHandler.advance();
                    fileHandler.advance();
                    symbolLexeme = ">=";
                    symbolType = TokenType.SRS_GREATER_THAN_OR_EQUAL;
                } else {
                    fileHandler.advance();
                    symbolLexeme = ">";
                    symbolType = TokenType.SRS_GREATER_THAN;
                }
                break;
            case '.':
                symbolLexeme = ".";
                fileHandler.advance();
                symbolType = TokenType.UNKNOWN;
                break;
            default:
                symbolLexeme = String.valueOf(currentChar).toUpperCase();
                fileHandler.advance();
                break;
        }
        return new Token(symbolType, symbolLexeme, tokenLine, tokenColumn);
    }
}
