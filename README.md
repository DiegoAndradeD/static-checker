# Static Checker

Este é um projeto educacional desenvolvido para a disciplina de **Compiladores** da [**Universidade Católica do Salvador - UCSal**](https://www.ucsal.br/). O objetivo é implementar um *Static Checker* para a linguagem de programação educacional **CangaCode2025-1**. O projeto realiza análise léxica completa, gerenciamento da tabela de símbolos e parte da análise sintática. Atua como um verificador estrutural, controlando o processo de análise do código fonte. Ao final, gera relatórios detalhados sobre os tokens identificados (`.LEX`) e o conteúdo da tabela de símbolos (`.TAB`).

## Sobre a Linguagem CangaCode2025-1
* **Case-insensitivity:** Identificadores e palavras-chave não diferenciam maiúsculas de minúsculas (são tratados internamente como maiúsculos).
* **Tipos de Dados:** Suporta INTEGER, REAL, STRING, BOOLEAN, CHARACTER, VOID e vetores (arrays) desses tipos.
* **Estrutura de Programa:** Organizada em blocos `PROGRAM`, `DECLARATIONS`, e `FUNCTIONS`.
* **Comandos:** Inclui atribuição, `IF-THEN-ELSE-ENDIF`, `WHILE-ENDWHILE`, `PRINT`, `RETURN`, e `BREAK`.
* **Comentários:** Suporta comentários de linha (`//`) e de bloco (`/* ... */`).

## Funcionalidades do Static Checker
1.  **Análise Léxica Completa:**
    * Identificação e classificação de todos os átomos da linguagem (Palavras Reservadas, Símbolos Reservados, Identificadores, Constantes Numéricas, Literais de String e Caractere).
    * Tratamento de espaços em branco e comentários (filtrados e atuando como delimitadores).
    * Implementação do "Filtro de Primeiro Nível": caracteres inválidos encontrados durante a formação de um átomo são ignorados sem interromper a formação do átomo.
    * Aplicação de regras de truncamento de lexemas (32 caracteres para o token efetivo, 35 para display na tabela de símbolos).
    * Geração de uma lista de tokens detalhada.

2.  **Tabela de Símbolos:**
    * Armazenamento de informações sobre todos os identificadores únicos encontrados.
    * Atributos por símbolo: lexema (original, truncado para display), código do átomo, categoria ("programName", "functionName", "variable", "parameter"), tipo de dado (inferido das declarações, ex: "IN", "FP", "AI"), e as primeiras cinco linhas de aparição.
    * Uso de uma tabela separada e pré-carregada para palavras e símbolos reservados.

3.  **Análise Sintática Parcial (Parser como Controlador):**
    * Atua como o orquestrador principal do processo de análise após a tokenização.
    * Verifica a estrutura de alto nível do programa (`PROGRAM ... ENDPROGRAM`).
    * Processa blocos de `DECLARATIONS`, identificando variáveis, seus tipos e se são vetores, atualizando a Tabela de Símbolos.
    * Processa blocos de `FUNCTIONS`, identificando nomes de funções, tipos de retorno e parâmetros (com seus tipos), atualizando a Tabela de Símbolos.
    * Reconhece e valida a estrutura básica dos seguintes comandos:
        * Atribuição (`variavel := expressaoSimplificada ;`)
        * `PRINT expressaoSimplificada [, expressaoSimplificada]* ;`
        * `RETURN [expressaoSimplificada] ;`
        * `IF (condicaoSimplificada) [THEN_IDN] Comandos [ELSE Comandos] ENDIF`
        * `WHILE (condicaoSimplificada) Comandos ENDWHILE`
        * `BREAK ;`
    * Realiza um tratamento simplificado de expressões aritméticas e lógicas (reconhecimento de fatores e operadores básicos).

4.  **Geração de Relatórios:**
    * Cria um arquivo `.LEX` contendo o relatório da análise léxica (token a token).
    * Cria um arquivo `.TAB` contendo o relatório da tabela de símbolos.
    * Os relatórios incluem cabeçalhos com informações da equipe e do arquivo fonte analisado, e seguem o formato especificado.

## Tecnologias Utilizadas
* **Linguagem:** Java SE 21 (OpenJDK)
* **Build Tool:** Apache Maven (para estrutura de projeto e gerenciamento de dependências, embora o projeto não tenha feito dependências externas complexas até sua atual versão).
* **IDE:** Visual Studio Code.

## Estrutura do Projeto
O projeto está organizado na seguinte estrutura de pacotes :
br/

- **App.java** - Ponto de entrada com menu interativo
- **config/**
  - **Constants.java** - Constantes globais
- **lexer/**
  - **Lexer.java** - Analisador Léxico
  - **Token.java** - Estrutura do Token
  - **TokenType.java** - Enumeração dos tipos de token e seus códigos
- **parser/**
  - **Parser.java** - Analisador Sintático Parcial / Controlador
- **reserved/**
  - **ReservedTable.java** - Tabela de Palavras Reservadas
- **symbolTable/**
  - **Symbol.java** - Estrutura de um Símbolo
  - **SymbolTable.java** - Gerenciador da Tabela de Símbolos
- **utils/**
  - **FileHandler.java** - Leitura e manipulação do arquivo fonte
  - **ReportGenerator.java** - Geração dos relatórios .LEX e .TAB



## Como Compilar e Executar

### Pré-requisitos
* Java Development Kit (JDK) versão 21 ou superior instalado e configurado.
* Apache Maven (opcional, se for usar o `pom.xml` para compilar).

### Compilando o Projeto
* **Com Maven:**
    No diretório raiz do projeto (onde está o `pom.xml`), execute:
    ```bash
    mvn clean compile package
    ```
    Isso gerará um arquivo JAR executável na pasta `target/` (ex: `staticchecker.jar`).


### Executando o Static Checker
* **Com o JAR gerado pelo Maven:**
    ```bash
    java -jar target/staticchecker.jar
    ```

> **Observação:** Você também pode executar o arquivo `app` diretamente pela sua IDE de preferência, como IntelliJ IDEA, Eclipse ou VS Code, facilitando o desenvolvimento e testes sem a necessidade de gerar o JAR a cada alteração.

Ao executar, um menu interativo será apresentado:
1.  **Usar arquivo de teste padrão ('Teste.251' no diretório atual):** O sistema tentará carregar `Teste.251` do diretório de onde foi executado.
2.  **Informar nome do arquivo (será procurado no diretório atual, não digite .251):** Você digita o nome base (ex: `MeuCodigo`) e o sistema procura por `MeuCodigo.251`.
3.  **Informar caminho do arquivo (extensão .251 opcional):** Você pode fornecer um caminho relativo ou absoluto. Se a extensão `.251` for omitida, o sistema a adicionará.
4.  **Sair.**

Após a análise, os arquivos `.LEX` e `.TAB` serão gerados no mesmo diretório do arquivo de entrada processado.

## Funcionalidades Detalhadas dos Módulos Principais

### `Lexer.java`
O analisador léxico é o coração deste projeto, assim como sua primeira fase de alta importância.

* **`getNextToken()`:** Método central que, a cada chamada, retorna o próximo token válido do fluxo de entrada.
* **`skipWhitespaceAndComments()`:** Remove espaços em branco, tabulações, novas linhas e comentários de linha (`//`) e de bloco (`/* */`) antes da tentativa de formação de um átomo.
* **`isCharToFilter()`:** Implementa o "filtro de primeiro nível", identificando caracteres que não pertencem à linguagem e não são delimitadores padrão. Esses caracteres são ignorados durante a formação de identificadores (permitindo que o átomo continue a ser formado) ou pulados se estiverem entre tokens.
* **Reconhecimento de Átomos:**
    * **Identificadores/Palavras Reservadas (`tryParseIdentifierOrKeyword`)**: Lê sequências alfanuméricas (iniciadas por letra ou `_`). Converte para maiúsculas e consulta a `ReservedTable`. Se não for palavra reservada, é um identificador (atualmente `IDN_VARIABLE` pelo lexer) e é processado pela `SymbolTable`.
    * **Números (`tryParseNumber`)**: Reconhece constantes inteiras (`IDN_INT_CONST`) e de ponto flutuante (`IDN_REAL_CONST`), incluindo notação científica simplificada (ex: `1.2e3`, `0.5E-1`).
    * **Literais de String (`tryParseStringLiteral`)**: Reconhece sequências entre aspas duplas (`"`), limitadas a uma única linha. O lexema inclui as aspas.
    * **Literais de Caractere (`tryParseCharLiteral`)**: Reconhece a forma `'<letra>'`. O lexema inclui as aspas.
    * **Símbolos (`parseSymbolOrUnknown`)**: Um `switch` trata todos os símbolos reservados de um ou dois caracteres (ex: `:=`, `==`, `;`). Se nenhum padrão for reconhecido, um token `UNKNOWN` é gerado.
* **Truncamento:** Identificadores e outros átomos longos são truncados para 32 caracteres para o lexema do token e chave da tabela, e para 35 caracteres para o lexema de display armazenado no objeto `Symbol`.

### `SymbolTable.java` e `Symbol.java`
* **`Symbol.java`**: Define a estrutura de uma entrada na tabela de símbolos. Atributos incluem:
    * `displayLexeme`: O lexema como será exibido (até 35 caracteres, maiúsculo para identificadores).
    * `tokenType`: O tipo de token original do lexer (ex: `IDN_VARIABLE`).
    * `category`: "programName", "functionName", "variable", "parameter" (definido pelo `Parser`).
    * `dataType`: "IN", "FP", "AI", "ST", etc. (definido pelo `Parser`).
    * `originalLength`: Comprimento do lexema válido coletado antes do truncamento para 32/35.
    * `lineAppearances`: Lista das primeiras 5 linhas onde o símbolo aparece.
* **`SymbolTable.java`**: Gerencia os símbolos.
    * **`addOrGetSymbol(...)`**: Adiciona um novo símbolo ou atualiza as aparições de um existente. Usa o lexema efetivo (truncado em 32 chars) como chave para garantir unicidade e retorna o índice do símbolo na tabela.

### `Parser.java`
Atua como o controlador principal e implementa a "parte da análise sintática".
* **`check()`**: Orquestra a análise da estrutura global do programa: `PROGRAM stmt`, `DECLARATIONS block`, `FUNCTIONS block`, lista de comandos principal, e `ENDPROGRAM stmt`.
* **Métodos de Parse de Bloco:**
    * `parseProgramStatement()`
    * `parseDeclarationsBlock()` (chama `parseVariableDeclarationLine()`)
    * `parseFunctionsBlock()` (chama `parseSingleFunctionDeclaration()`)
* **`parseVariableDeclarationLine()`**: Processa `VARTYPE Tipo [[]] : lista_ids ;`, atualizando `dataType` e `category` na `SymbolTable`.
* **`parseSingleFunctionDeclaration()`**: Processa `FUNCTYPE TipoRet : NomeFunc (PARAMS) corpoDaFunc ENDFUNCTION`. Atualiza `dataType` (tipo de retorno) e `category` para o nome da função. Chama `parseParameterList()`.
* **`parseParameterList()`**: Processa `(PARAMTYPE Tipo : idParam, ... )`, atualizando `dataType` e `category` para parâmetros.
* **`parseCommandList(stopTokens...)`**: Método recursivo que processa uma sequência de comandos até encontrar um dos `stopTokens` especificados. Despacha para métodos de parse de comandos individuais.
* **Métodos de Parse de Comando Individual:**
    * `parseAssignmentCommand()`
    * `parsePrintCommand()` (lida com múltiplas expressões separadas por vírgula)
    * `parseReturnCommand()`
    * `parseIfCommand()` (lida com `(Cond)`, `THEN_IDN`, `ELSE`, `ENDIF` e chama `parseCommandList` para os blocos)
    * `parseWhileCommand()` (lida com `(Cond)`, e chama `parseCommandList` para o corpo)
    * `parseBreakCommand()`
* **Análise de Expressão Simplificada (`parseAritmExp`, `parseLogicalExp`, `parseFactor`)**: Reconhece uma sequência de `Fator [Operador Fator]*` e condições simples `AritmExp [OpRelacional AritmExp]`. Não implementa precedência de operadores ou parênteses dentro das expressões.

### `ReportGenerator.java`
Gera os relatórios `.LEX` e `.TAB`.
* Usa `Constants.java` para informações de cabeçalho.
* **`.LEX`**: Lista cada token com seu lexema (truncado para IDNs), código do átomo (de `TokenType.getCode()`), índice na tabela de símbolos (se aplicável), e número da linha.
* **`.TAB`**: Lista cada símbolo da `SymbolTable` com: "Entrada" (índice), "Codigo" (do `TokenType` do símbolo), "Lexeme" (display, até 35 chars), "QtdCharAntesTrunc", "QtdCharDepoisTrunc", "TipoSimb" (mostrando `dataType` ou `category`), e "Linhas" (formatado com `{}` para uma linha, `()` para múltiplas).

## Limitações Conhecidas
* **Parsing de Expressão:** O parser de expressões aritméticas e lógicas é simplificado e não lida com precedência completa de operadores, associatividade, ou parênteses para agrupamento dentro das expressões.
* **Recuperação de Erro Sintático:** O tratamento de erros no `Parser` é básico (imprime uma mensagem e tenta avançar o token). Não há mecanismos sofisticados de recuperação de erro.
* **Análise Semântica:** A análise semântica é limitada à atribuição de tipos e categorias com base em declarações. Não há verificação de tipos em expressões, verificação de uso de variáveis não declaradas em todos os contextos, ou resolução de escopo completa (além do global implícito).
* **Cobertura Gramatical:** O `Parser` foca nas estruturas principais e não implementa todas as nuances ou produções alternativas da gramática formal.
* **Filtro de Caracteres Inválidos:** Embora implementado para identificadores, sua aplicação e o impacto exato no `originalLength` para outros tipos de átomos (como números) não foram exaustivamente explorados.

## Autor(es)
* [Diego Andrade Deiró]
* [Joao Victor Aziz Lima]
* [Denilson Xavier Oliveira]
* [Gabriel Da Silva Azevedo]

## Licença
Licença MIT (MIT)

Copyright (c) 2025 Diego Andrade Deiró

É concedida permissão, gratuitamente, a qualquer pessoa que obtenha uma cópia deste software e dos arquivos de documentação associados (o "Software"), para lidar com o Software sem restrições, incluindo, entre outras, os direitos de usar, copiar, modificar, mesclar, publicar, distribuir, sublicenciar e/ou vender cópias do Software, e para permitir que as pessoas a quem o Software é fornecido o façam, sujeito às seguintes condições:

O aviso de direitos autorais acima e esta permissão devem ser incluídos em todas as cópias ou partes substanciais do Software.

O SOFTWARE É FORNECIDO "NO ESTADO EM QUE SE ENCONTRA", SEM GARANTIA DE QUALQUER TIPO, EXPRESSA OU IMPLÍCITA, INCLUINDO, MAS NÃO SE LIMITANDO ÀS GARANTIAS DE COMERCIALIZAÇÃO, ADEQUAÇÃO A UM DETERMINADO FIM E NÃO VIOLAÇÃO. EM NENHUMA HIPÓTESE OS AUTORES OU TITULARES DOS DIREITOS AUTORAIS SERÃO RESPONSÁVEIS POR QUALQUER RECLAMAÇÃO, DANOS OU OUTRA RESPONSABILIDADE, SEJA EM UMA AÇÃO CONTRATUAL, ATO ILÍCITO OU DE OUTRA FORMA, DECORRENTE DE, DE OU EM CONEXÃO COM O SOFTWARE OU O USO OU OUTRAS NEGOCIAÇÕES NO SOFTWARE.
