package br.ucsal.compiladores.parser;

import br.ucsal.compiladores.lexer.Token;
import br.ucsal.compiladores.lexer.TokenType;
import br.ucsal.compiladores.symbolTable.Symbol;
import br.ucsal.compiladores.symbolTable.SymbolTable;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Parser {
    private final List<Token> tokens;
    private final SymbolTable symbolTable;
    private int currentTokenIndex;
    private Token currentToken;

    private static final Map<TokenType, String> TYPE_SPEC_TO_DATA_TYPE_MAP = new HashMap<>();
    static {
        TYPE_SPEC_TO_DATA_TYPE_MAP.put(TokenType.PRS_INTEGER, "IN");
        TYPE_SPEC_TO_DATA_TYPE_MAP.put(TokenType.PRS_REAL, "FP");
        TYPE_SPEC_TO_DATA_TYPE_MAP.put(TokenType.PRS_STRING, "ST");
        TYPE_SPEC_TO_DATA_TYPE_MAP.put(TokenType.PRS_BOOLEAN, "BL");
        TYPE_SPEC_TO_DATA_TYPE_MAP.put(TokenType.PRS_CHARACTER, "CH");
        TYPE_SPEC_TO_DATA_TYPE_MAP.put(TokenType.PRS_VOID, "VD");
    }

    public Parser(List<Token> tokens, SymbolTable symbolTable) {
        this.tokens = tokens;
        this.symbolTable = symbolTable;
        this.currentTokenIndex = 0;
        if (!tokens.isEmpty()) {
            this.currentToken = tokens.get(currentTokenIndex);
        } else {
            this.currentToken = new Token(TokenType.EOF, "", 0, 0);
        }
    }

    private void advance() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        }
    }

    private Token consume(TokenType expectedType) {
        if (match(expectedType)) {
            Token consumedToken = currentToken;
            advance();
            return consumedToken;
        } else {
            System.err.println("Erro Sintático: Esperado " + expectedType +
                    " mas encontrado " + currentToken.getType() +
                    " ('" + currentToken.getLexeme() + "') na linha " + currentToken.getLine() +
                    " coluna " + currentToken.getColumn());
            Token errorToken = currentToken;
            advance();
            return errorToken;
        }
    }

    private Token consumeIdentifier(String expectedLexeme) {
        if (match(TokenType.IDN_VARIABLE) && currentToken.getLexeme().equals(expectedLexeme.toUpperCase())) {
            Token consumedToken = currentToken;
            advance();
            return consumedToken;
        } else {
            System.err.println("Erro Sintático: Esperado IDENTIFICADOR '" + expectedLexeme + "'" +
                    " mas encontrado " + currentToken.getType() +
                    " ('" + currentToken.getLexeme() + "') na linha " + currentToken.getLine() +
                    " coluna " + currentToken.getColumn());
            Token errorToken = currentToken;
            advance();
            return errorToken;
        }
    }

    private boolean match(TokenType type) {
        return currentToken.getType() == type;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (match(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchTypeSpecification() {
        return TYPE_SPEC_TO_DATA_TYPE_MAP.containsKey(currentToken.getType());
    }

    private String getDataTypeString(TokenType typeSpecToken, boolean isArray) {
        String baseType = TYPE_SPEC_TO_DATA_TYPE_MAP.getOrDefault(typeSpecToken, "ERR");
        if (baseType.equals("ERR"))
            return "N/D";
        if (baseType.equals("VD") && isArray)
            return "ERR_ARRAY_OF_VOID";
        if (isArray) {
            if (baseType.equals("IN"))
                return "AI";
            if (baseType.equals("FP"))
                return "AF";
            if (baseType.equals("ST"))
                return "AS";
            if (baseType.equals("BL"))
                return "AB";
            if (baseType.equals("CH"))
                return "AC";
        }
        return baseType;
    }

    private void parseVariableDeclarationLine() {
        if (matchTypeSpecification()) {
            Token typeSpecToken = consume(currentToken.getType());
            boolean isArray = false;

            if (match(TokenType.SRS_LEFT_BRACKET)) {
                consume(TokenType.SRS_LEFT_BRACKET);
                if (match(TokenType.SRS_RIGHT_BRACKET)) {
                    consume(TokenType.SRS_RIGHT_BRACKET);
                    isArray = true;
                } else {
                    System.err.println("Erro Sintático: Esperado ']' após '[' na declaração de vetor na linha "
                            + currentToken.getLine());
                }
            }

            String dataType = getDataTypeString(typeSpecToken.getType(), isArray);
            if (dataType.equals("ERR_ARRAY_OF_VOID")) {
                System.err.println("Erro Semântico: Declaração de array do tipo VOID não é permitida na linha "
                        + typeSpecToken.getLine());
                while (currentToken.getType() != TokenType.SRS_SEMICOLON && currentToken.getType() != TokenType.EOF) {
                    advance();
                }
                if (match(TokenType.SRS_SEMICOLON))
                    consume(TokenType.SRS_SEMICOLON);
                return;
            }

            if (match(TokenType.SRS_COLON)) {
                consume(TokenType.SRS_COLON);
                do {
                    if (match(TokenType.IDN_VARIABLE)) {
                        Token varToken = consume(TokenType.IDN_VARIABLE);
                        if (varToken.getSymbolIndex() != null) {
                            Symbol varSymbol = symbolTable.getSymbolByIndex(varToken.getSymbolIndex());
                            if (varSymbol != null) {
                                varSymbol.setDataType(dataType);
                                if (varSymbol.getCategory() == null || !varSymbol.getCategory().equals("parameter")) {
                                    varSymbol.setCategory("variable");
                                }
                                System.out.println("Parser: Variável '" + varToken.getLexeme() + (isArray ? "[]" : "")
                                        + "' declarada com tipo '" + dataType + "'");
                            }
                        }
                    } else {
                        System.err.println(
                                "Erro Sintático: Esperado IDENTIFICADOR DE VARIÁVEL após ':' ou ',' na declaração na linha "
                                        + currentToken.getLine());
                        while (currentToken.getType() != TokenType.SRS_COMMA &&
                                currentToken.getType() != TokenType.SRS_SEMICOLON &&
                                currentToken.getType() != TokenType.EOF) {
                            advance();
                        }
                    }
                    if (match(TokenType.SRS_COMMA)) {
                        consume(TokenType.SRS_COMMA);
                    } else {
                        break;
                    }
                } while (true);
                if (match(TokenType.SRS_SEMICOLON)) {
                    consume(TokenType.SRS_SEMICOLON);
                } else {
                    System.err.println("Erro Sintático: Esperado ';' no final da declaração de variável na linha "
                            + currentToken.getLine());
                }
            } else {
                System.err.println(
                        "Erro Sintático: Esperado ':' após ESPECIFICADOR_DE_TIPO na declaração de variável na linha "
                                + currentToken.getLine());
            }
        } else {
            System.err.println(
                    "Erro Sintático: Esperado ESPECIFICADOR_DE_TIPO após VARTYPE na linha " + currentToken.getLine());
        }
    }

    private void parseParameterList() {
        if (match(TokenType.PRS_PARAM_TYPE)) {
            do {
                consume(TokenType.PRS_PARAM_TYPE);
                boolean isArrayParam = false;

                if (matchTypeSpecification()) {
                    Token typeSpecToken = consume(currentToken.getType());
                    String paramDataType = getDataTypeString(typeSpecToken.getType(), isArrayParam);

                    if (match(TokenType.SRS_COLON)) {
                        consume(TokenType.SRS_COLON);

                        if (match(TokenType.IDN_VARIABLE)) {
                            Token paramToken = consume(TokenType.IDN_VARIABLE);
                            if (paramToken.getSymbolIndex() != null) {
                                Symbol paramSymbol = symbolTable.getSymbolByIndex(paramToken.getSymbolIndex());
                                if (paramSymbol != null) {
                                    paramSymbol.setDataType(paramDataType);
                                    paramSymbol.setCategory("parameter");
                                    System.out.println("Parser: Parâmetro '" + paramToken.getLexeme()
                                            + "' declarada com tipo '" + paramDataType + "'");
                                }
                            }
                        } else {
                            System.err.println("Erro Sintático: Esperado IDENTIFICADOR_DE_PARAMETRO após ':' na linha "
                                    + currentToken.getLine());
                            while (currentToken.getType() != TokenType.SRS_COMMA &&
                                    currentToken.getType() != TokenType.SRS_RIGHT_PARENTHESIS &&
                                    currentToken.getType() != TokenType.EOF) {
                                advance();
                            }
                        }
                    } else {
                        System.err.println(
                                "Erro Sintático: Esperado ':' após ESPECIFICADOR_DE_TIPO do parâmetro na linha "
                                        + currentToken.getLine());
                    }
                } else {
                    System.err.println("Erro Sintático: Esperado ESPECIFICADOR_DE_TIPO após PARAMTYPE na linha "
                            + currentToken.getLine());
                }

                if (match(TokenType.SRS_COMMA)) {
                    consume(TokenType.SRS_COMMA);
                    if (!match(TokenType.PRS_PARAM_TYPE)) {
                        System.err
                                .println("Erro Sintático: Esperado PARAMTYPE após ',' na lista de parâmetros na linha "
                                        + currentToken.getLine());
                        break;
                    }
                } else {
                    break;
                }
            } while (true);
        }
        if (match(TokenType.SRS_RIGHT_PARENTHESIS)) {
            consume(TokenType.SRS_RIGHT_PARENTHESIS);
        } else {
            System.err.println("Erro Sintático: Esperado ')' para fechar a lista de parâmetros na linha "
                    + currentToken.getLine());
        }
    }

    private void parseFactor() {
        System.out.print(" (Fator: ");
        if (match(TokenType.IDN_VARIABLE) ||
                match(TokenType.IDN_INT_CONST) ||
                match(TokenType.IDN_REAL_CONST) ||
                match(TokenType.PRS_TRUE) ||
                match(TokenType.PRS_FALSE) ||
                match(TokenType.IDN_STRING_CONST) ||
                match(TokenType.IDN_CHAR_CONST)) {
            System.out.print(currentToken.getLexeme());
            advance();
        } else {
            System.out.print("<<Fator Inválido: " + currentToken.getLexeme() + ">>");
        }
        System.out.print(") ");
    }

    private void parseAritmExp() {
        parseFactor();
        while (match(TokenType.SRS_PLUS, TokenType.SRS_MINUS,
                TokenType.SRS_MULTIPLICATION, TokenType.SRS_DIVISION, TokenType.SRS_MODULO)) {
            Token operator = currentToken;
            System.out.print(operator.getLexeme() + " ");
            advance();
            parseFactor();
        }
    }

    private void parseLogicalExp() {
        System.out.print(" (ExprLógica: ");
        parseAritmExp();

        if (match(TokenType.SRS_EQUAL_COMPARISON, TokenType.SRS_NOT_EQUAL,
                TokenType.SRS_LESS_THAN, TokenType.SRS_LESS_THAN_OR_EQUAL,
                TokenType.SRS_GREATER_THAN, TokenType.SRS_GREATER_THAN_OR_EQUAL)) {
            Token relOp = currentToken;
            System.out.print(relOp.getLexeme() + " ");
            advance();
            parseAritmExp();
        }
        System.out.print(") ");
    }

    private void parseAssignmentCommand() {
        Token variableToken = consume(TokenType.IDN_VARIABLE);
        System.out.println("Parser: Comando de atribuição para variável '" + variableToken.getLexeme() + "'");
        if (match(TokenType.SRS_ASSIGNMENT)) {
            consume(TokenType.SRS_ASSIGNMENT);
        } else {
            System.err.println("Erro Sintático: Esperado ':=' após variável '" + variableToken.getLexeme() +
                    "' no comando de atribuição na linha " + variableToken.getLine());
            while (currentToken.getType() != TokenType.SRS_SEMICOLON && currentToken.getType() != TokenType.EOF) {
                advance();
            }
        }
        System.out.print("Parser: Lendo expressão da atribuição: ");
        parseAritmExp();
        System.out.println();
        if (match(TokenType.SRS_SEMICOLON)) {
            consume(TokenType.SRS_SEMICOLON);
        } else {
            System.err.println("Erro Sintático: Esperado ';' no final do comando de atribuição iniciado na linha "
                    + variableToken.getLine());
        }
    }

    private void parsePrintCommand() {
        Token printToken = consume(TokenType.PRS_PRINT);
        System.out.println("Parser: Comando PRINT encontrado na linha " + printToken.getLine());
        System.out.print("Parser: Lendo expressão(ões) do PRINT: ");

        boolean firstExpression = true;
        do {
            if (!firstExpression && match(TokenType.SRS_COMMA)) {
                System.out.print(currentToken.getLexeme() + " ");
                consume(TokenType.SRS_COMMA);
            } else if (!firstExpression && !match(TokenType.SRS_SEMICOLON)) {
                System.err.println("Erro Sintático: Esperado ',' ou ';' após expressão no comando PRINT na linha "
                        + currentToken.getLine());
                break;
            }

            parseAritmExp();
            firstExpression = false;

            if (!match(TokenType.SRS_COMMA)) {
                break;
            }
        } while (true);

        System.out.println();

        if (match(TokenType.SRS_SEMICOLON)) {
            consume(TokenType.SRS_SEMICOLON);
        } else {
            System.err.println(
                    "Erro Sintático: Esperado ';' no final do comando PRINT iniciado na linha " + printToken.getLine() +
                            ". Encontrado: " + currentToken.getLexeme());
        }
    }

    private void parseReturnCommand() {
        Token returnToken = consume(TokenType.PRS_RETURN);
        System.out.println("Parser: Comando RETURN encontrado na linha " + returnToken.getLine());
        if (currentToken.getType() != TokenType.SRS_SEMICOLON && currentToken.getType() != TokenType.EOF) {
            System.out.print("Parser: Lendo expressão do RETURN: ");
            parseAritmExp();
            System.out.println();
        } else {
            System.out.println("Parser: RETURN sem expressão.");
        }
        if (match(TokenType.SRS_SEMICOLON)) {
            consume(TokenType.SRS_SEMICOLON);
        } else {
            System.err.println("Erro Sintático: Esperado ';' no final do comando RETURN iniciado na linha "
                    + returnToken.getLine());
        }
    }

    private void parseIfCommand() {
        Token ifToken = consume(TokenType.PRS_IF);
        System.out.println("Parser: Comando IF encontrado na linha " + ifToken.getLine());

        if (!match(TokenType.SRS_LEFT_PARENTHESIS)) {
            System.err.println("Erro Sintático: Esperado '(' após IF na linha " + ifToken.getLine());
            while (currentToken.getType() != TokenType.PRS_ENDIF &&
                    currentToken.getType() != TokenType.SRS_SEMICOLON &&
                    currentToken.getType() != TokenType.EOF) {
                advance();
            }
            if (match(TokenType.PRS_ENDIF))
                consume(TokenType.PRS_ENDIF);
            return;
        }
        consume(TokenType.SRS_LEFT_PARENTHESIS);

        System.out.print("Parser: Lendo condição do IF:");
        parseLogicalExp();
        System.out.println();

        if (!match(TokenType.SRS_RIGHT_PARENTHESIS)) {
            System.err.println("Erro Sintático: Esperado ')' para fechar condição do IF na linha " + ifToken.getLine()
                    + ". Encontrado: " + currentToken);
            while (currentToken.getType() != TokenType.PRS_ENDIF && currentToken.getType() != TokenType.EOF) {
                advance();
            }
            if (match(TokenType.PRS_ENDIF))
                consume(TokenType.PRS_ENDIF);
            return;
        }
        consume(TokenType.SRS_RIGHT_PARENTHESIS);

        if (currentToken.getType() == TokenType.IDN_VARIABLE && currentToken.getLexeme().equals("THEN")) {
            System.out.println("Parser: Consumindo 'THEN' (como IDN_VARIABLE) após condição do IF.");
            consumeIdentifier("THEN");
        }

        System.out.println("Parser: Processando bloco THEN do IF iniciado na linha " + ifToken.getLine());
        parseCommandList(TokenType.PRS_ELSE, TokenType.PRS_ENDIF);

        if (match(TokenType.PRS_ELSE)) {
            consume(TokenType.PRS_ELSE);
            System.out.println("Parser: Processando bloco ELSE do IF iniciado na linha " + ifToken.getLine());
            parseCommandList(TokenType.PRS_ENDIF);
        }

        if (match(TokenType.PRS_ENDIF)) {
            consume(TokenType.PRS_ENDIF);
            System.out.println("Parser: Fim do comando IF (ENDIF) da linha " + ifToken.getLine());
        } else {
            System.err.println("Erro Sintático: Esperado ENDIF para finalizar comando IF iniciado na linha "
                    + ifToken.getLine() + ". Encontrado: " + currentToken);
        }
    }

    private void parseWhileCommand() {
        Token whileToken = consume(TokenType.PRS_WHILE);
        System.out.println("Parser: Comando WHILE encontrado na linha " + whileToken.getLine());

        if (!match(TokenType.SRS_LEFT_PARENTHESIS)) {
            System.err.println("Erro Sintático: Esperado '(' após WHILE na linha " + whileToken.getLine());
            while (currentToken.getType() != TokenType.PRS_ENDWHILE && currentToken.getType() != TokenType.EOF) {
                advance();
            }
            if (match(TokenType.PRS_ENDWHILE))
                consume(TokenType.PRS_ENDWHILE);
            return;
        }
        consume(TokenType.SRS_LEFT_PARENTHESIS);

        System.out.print("Parser: Lendo condição do WHILE:");
        parseLogicalExp();
        System.out.println();

        if (!match(TokenType.SRS_RIGHT_PARENTHESIS)) {
            System.err.println("Erro Sintático: Esperado ')' para fechar condição do WHILE na linha "
                    + whileToken.getLine() + ". Encontrado: " + currentToken);
            while (currentToken.getType() != TokenType.PRS_ENDWHILE && currentToken.getType() != TokenType.EOF) {
                advance();
            }
            if (match(TokenType.PRS_ENDWHILE))
                consume(TokenType.PRS_ENDWHILE);
            return;
        }
        consume(TokenType.SRS_RIGHT_PARENTHESIS);

        System.out.println("Parser: Processando corpo do WHILE iniciado na linha " + whileToken.getLine());
        parseCommandList(TokenType.PRS_ENDWHILE);

        if (match(TokenType.PRS_ENDWHILE)) {
            consume(TokenType.PRS_ENDWHILE);
            System.out.println("Parser: Fim do comando WHILE (ENDWHILE) da linha " + whileToken.getLine());
        } else {
            System.err.println("Erro Sintático: Esperado ENDWHILE para finalizar comando WHILE iniciado na linha "
                    + whileToken.getLine() + ". Encontrado: " + currentToken);
        }
    }

    private void parseBreakCommand() {
        Token breakToken = consume(TokenType.PRS_BREAK);
        System.out.println("Parser: Comando BREAK encontrado na linha " + breakToken.getLine());

        if (match(TokenType.SRS_SEMICOLON)) {
            consume(TokenType.SRS_SEMICOLON);
        } else {
            System.err.println("Erro Sintático: Esperado ';' após comando BREAK na linha " + breakToken.getLine());
        }
    }

    private void parseCommandList(TokenType... stopTokens) {
        Set<TokenType> stops = Set.of(stopTokens);
        while (currentToken.getType() != TokenType.EOF && !stops.contains(currentToken.getType())) {
            if (match(TokenType.IDN_VARIABLE)) {
                parseAssignmentCommand();
            } else if (match(TokenType.PRS_PRINT)) {
                parsePrintCommand();
            } else if (match(TokenType.PRS_RETURN)) {
                parseReturnCommand();
            } else if (match(TokenType.PRS_IF)) {
                parseIfCommand();
            } else if (match(TokenType.PRS_WHILE)) {
                parseWhileCommand();
            } else if (match(TokenType.PRS_BREAK)) {
                parseBreakCommand();
            } else {
                System.err.println("Erro Sintático: Comando desconhecido ou inesperado '" + currentToken.getLexeme() +
                        "' na linha " + currentToken.getLine() + " dentro de um bloco de comandos.");
                advance();
            }
        }
    }

    private void parseProgramStatement() {
        if (match(TokenType.PRS_PROGRAM)) {
            consume(TokenType.PRS_PROGRAM);
            if (match(TokenType.IDN_VARIABLE)) {
                Token programNameToken = consume(TokenType.IDN_VARIABLE);
                System.out.println("Parser: Nome do programa identificado: " + programNameToken.getLexeme());
                if (programNameToken.getSymbolIndex() != null) {
                    Symbol progSymbol = symbolTable.getSymbolByIndex(programNameToken.getSymbolIndex());
                    if (progSymbol != null) {
                        progSymbol.setCategory("programName");
                    }
                }
                if (match(TokenType.SRS_SEMICOLON)) {
                    consume(TokenType.SRS_SEMICOLON);
                } else {
                    System.err.println(
                            "Erro Sintático: Esperado ';' após o nome do programa na linha " + currentToken.getLine());
                }
            } else {
                System.err.println(
                        "Erro Sintático: Esperado NOME_DO_PROGRAMA após PROGRAM na linha " + currentToken.getLine());
            }
        } else {
            System.err.println("Erro Sintático: Programa deve iniciar com a palavra reservada 'PROGRAM' na linha "
                    + currentToken.getLine());
            while (!match(TokenType.PRS_DECLARATIONS, TokenType.PRS_FUNCTIONS, TokenType.PRS_END_PROGRAM,
                    TokenType.EOF)) {
                advance();
            }
        }
    }

    private void parseDeclarationsBlock() {
        if (match(TokenType.PRS_DECLARATIONS)) {
            System.out.println("Parser: Bloco DECLARATIONS encontrado.");
            consume(TokenType.PRS_DECLARATIONS);
            while (match(TokenType.PRS_VAR_TYPE)) {
                consume(TokenType.PRS_VAR_TYPE);
                parseVariableDeclarationLine();
            }
            if (match(TokenType.PRS_END_DECLARATIONS)) {
                consume(TokenType.PRS_END_DECLARATIONS);
                System.out.println("Parser: Fim do bloco DECLARATIONS.");
            } else {
                System.err.println("Erro Sintático: Esperado ENDDECLARATIONS para finalizar o bloco na linha "
                        + currentToken.getLine());
            }
        }
    }

    private void parseSingleFunctionDeclaration() {
        String functionReturnType = "N/D";
        if (matchTypeSpecification()) {
            Token typeSpecToken = consume(currentToken.getType());
            functionReturnType = getDataTypeString(typeSpecToken.getType(), false);
        } else {
            System.err.println(
                    "Erro Sintático: Esperado ESPECIFICADOR_DE_TIPO após FUNCTYPE na linha " + currentToken.getLine());
        }

        if (match(TokenType.SRS_COLON)) {
            consume(TokenType.SRS_COLON);
        } else {
            System.err.println("Erro Sintático: Esperado ':' após ESPECIFICADOR_DE_TIPO da função na linha "
                    + currentToken.getLine());
        }

        if (match(TokenType.IDN_VARIABLE)) {
            Token funcNameToken = consume(TokenType.IDN_VARIABLE);
            System.out.println("Parser: Nome de função identificado: " + funcNameToken.getLexeme());
            if (funcNameToken.getSymbolIndex() != null) {
                Symbol funcSymbol = symbolTable.getSymbolByIndex(funcNameToken.getSymbolIndex());
                if (funcSymbol != null) {
                    funcSymbol.setCategory("functionName");
                    funcSymbol.setDataType(functionReturnType);
                }
            }

            if (match(TokenType.SRS_LEFT_PARENTHESIS)) {
                consume(TokenType.SRS_LEFT_PARENTHESIS);
                parseParameterList();
            } else {
                System.err.println("Erro Sintático: Esperado '(' após nome da função '" + funcNameToken.getLexeme()
                        + "' na linha " + currentToken.getLine());
            }

            System.out.println("Parser: Processando corpo da função '" + funcNameToken.getLexeme() + "'.");
            parseCommandList(TokenType.PRS_ENDFUNCTION);

            if (match(TokenType.PRS_ENDFUNCTION)) {
                consume(TokenType.PRS_ENDFUNCTION);
                System.out.println("Parser: Fim da função '" + funcNameToken.getLexeme() + "'.");
            } else {
                System.err.println(
                        "Erro Sintático: Esperado ENDFUNCTION para finalizar a função '" + funcNameToken.getLexeme()
                                + "' na linha " + currentToken.getLine() + ". Encontrado: " + currentToken);
            }
        } else {
            System.err.println("Erro Sintático: Esperado NOME_DA_FUNCAO após ':' na linha " + currentToken.getLine());
        }
    }

    private void parseFunctionsBlock() {
        if (match(TokenType.PRS_FUNCTIONS)) {
            System.out.println("Parser: Bloco FUNCTIONS encontrado.");
            consume(TokenType.PRS_FUNCTIONS);
            while (match(TokenType.PRS_FUNC_TYPE)) {
                consume(TokenType.PRS_FUNC_TYPE);
                parseSingleFunctionDeclaration();
            }
            if (match(TokenType.PRS_END_FUNCTIONS)) {
                consume(TokenType.PRS_END_FUNCTIONS);
                System.out.println("Parser: Fim do bloco FUNCTIONS.");
            } else {
                System.err.println("Erro Sintático: Esperado ENDFUNCTIONS para finalizar o bloco na linha "
                        + currentToken.getLine());
            }
        }
    }

    public void check() {
        System.out.println("\nParser Iniciado: Verificando sequência de tokens...");

        parseProgramStatement();
        parseDeclarationsBlock();
        parseFunctionsBlock();

        if (currentToken.getType() != TokenType.PRS_END_PROGRAM && currentToken.getType() != TokenType.EOF) {
            System.out.println("Parser: Iniciando processamento de comandos do bloco principal.");
            parseCommandList(TokenType.PRS_END_PROGRAM);
        }

        if (match(TokenType.PRS_END_PROGRAM)) {
            consume(TokenType.PRS_END_PROGRAM);
            System.out.println("Parser: Fim do programa (ENDPROGRAM) encontrado.");
        } else {
            if (currentToken.getType() != TokenType.EOF) {
                System.err.println("Erro Sintático: Esperado ENDPROGRAM para finalizar o programa na linha "
                        + currentToken.getLine() + ". Encontrado: " + currentToken.getType());
            }
        }

        if (currentToken.getType() != TokenType.EOF) {
            System.err.println("Erro Sintático: Tokens residuais após o esperado fim do programa, começando com '"
                    + currentToken.getLexeme() + "' na linha " + currentToken.getLine());
        }

        System.out.println("Parser: Fim da verificação de tokens (EOF alcançado).");
    }
}
