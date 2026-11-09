package br.com.klang.parser;

import java.util.ArrayList;
import java.util.List;
import br.com.klang.lexer.*;

public class Parser {

    private final Lexer lexer;

    private Token tokenAtual;

    private final List<String> erros;
    private final List<String> warnings;

    public Parser(Lexer lexer) {

        this.lexer = lexer;

        this.erros = new ArrayList<>();
        this.warnings = new ArrayList<>();

        avancar();
    }

    // =========================================================
    // CONTROLE DOS TOKENS
    // =========================================================

    private void avancar() {
        tokenAtual = lexer.nextToken();
    }

    private boolean verificar(TokenType tipo) {

        return tokenAtual != null
                && tokenAtual.tipo == tipo;
    }

    private boolean verificarKeyword(Keyword keyword) {

        return tokenAtual != null
                && tokenAtual.tipo == TokenType.KEYWORD
                && tokenAtual.keyword == keyword;
    }

    private boolean aceitar(TokenType tipo) {

        if (verificar(tipo)) {
            avancar();
            return true;
        }

        return false;
    }

    private boolean aceitarKeyword(Keyword keyword) {

        if (verificarKeyword(keyword)) {
            avancar();
            return true;
        }

        return false;
    }

    private void consumir(TokenType tipo, String mensagem) {

        if (verificar(tipo)) {
            avancar();
        } else {
            erroSintatico(mensagem);
        }
    }

    private void consumirKeyword(Keyword keyword, String mensagem) {

        if (verificarKeyword(keyword)) {
            avancar();
        } else {
            erroSintatico(mensagem);
        }
    }

    private void erroSintatico(String mensagem) {

        if (tokenAtual != null) {

            String erro =
                    "Erro sintático na linha "
                    + tokenAtual.linha
                    + ", coluna "
                    + tokenAtual.coluna
                    + ": "
                    + mensagem
                    + " Encontrado: '"
                    + tokenAtual.lexema
                    + "'.";

            erros.add(erro);

        } else {

            String erro =
                    "Erro sintático no final do programa: "
                    + mensagem;

            erros.add(erro);
        }
    }

// =========================================================
// RECUPERAÇÃO DE ERROS
// =========================================================

private void sincronizarInstrucao() {

    while (tokenAtual != null) {

        if (verificar(TokenType.PONTO_E_VIRGULA)) {
            avancar();
            return;
        }

        if (verificarKeyword(Keyword.END)
                || verificarKeyword(Keyword.ELSE)
                || verificarKeyword(Keyword.UNTIL)) {
            return;
        }

        if (verificar(TokenType.ID)
                || verificarKeyword(Keyword.IF)
                || verificarKeyword(Keyword.WHILE)
                || verificarKeyword(Keyword.REPEAT)
                || verificarKeyword(Keyword.FOR)
                || verificarKeyword(Keyword.BREAK)
                || verificarKeyword(Keyword.CONTINUE)
                || verificarKeyword(Keyword.BEGIN)) {
            return;
        }

        avancar();
    }
}

    private void warning(String mensagem, Token token) {

        String aviso =
                "Warning na linha "
                + token.linha
                + ", coluna "
                + token.coluna
                + ": "
                + mensagem;

        warnings.add(aviso);
    }

    // =========================================================
    // PONTO DE ENTRADA
    // =========================================================

    public void analisar() {
    analisarSemImprimir();
    imprimirResultado();
}

    public void analisarSemImprimir() {

    programa();

    if (tokenAtual != null) {

        erroSintatico(
                "tokens encontrados após o final do programa."
        );
    }
    }

    // =========================================================
    // programa
    //
    // programa → PROGRAM ID; declaracoes BEGIN instruçoes END .
    // =========================================================

    private void programa() {

        consumirKeyword(
                Keyword.PROGRAM,
                "esperado 'PROGRAM'."
        );

        consumir(
                TokenType.ID,
                "esperado identificador do programa."
        );

        consumir(
                TokenType.PONTO_E_VIRGULA,
                "esperado ';' após o nome do programa."
        );

        declaracoes();

        consumirKeyword(
                Keyword.BEGIN,
                "esperado 'BEGIN'."
        );

        instrucoes();

        consumirKeyword(
                Keyword.END,
                "esperado 'END'."
        );

        consumir(
                TokenType.PONTO,
                "esperado '.' no final do programa."
        );
    }

    // =========================================================
    // declaracoes
    // =========================================================

    private void declaracoes() {

        declaracaoVariavel();

        declaracaoConstante();

        declProcedimento();
    }

    // =========================================================
    // VAR
    // =========================================================

    private void declaracaoVariavel() {

        if (!aceitarKeyword(Keyword.VAR)) {
            return;
        }

        while (verificar(TokenType.ID)) {

            declVar();
        }
    }

    private void declVar() {

        // Primeiro ID
        consumir(
                TokenType.ID,
                "esperado identificador na declaração."
        );

        // , ID
        while (aceitar(TokenType.VIRGULA)) {

            consumir(
                    TokenType.ID,
                    "esperado identificador após ','."
            );
        }

        consumir(
                TokenType.DOIS_PONTOS,
                "esperado ':' na declaração de variável."
        );

        tipo();

        consumir(
                TokenType.PONTO_E_VIRGULA,
                "esperado ';' após declaração de variável."
        );
    }

    // =========================================================
    // TIPOS
    // =========================================================

    private void tipo() {

        if (aceitarKeyword(Keyword.INTEGER)) {
            return;
        }

        if (aceitarKeyword(Keyword.REAL)) {
            return;
        }

        if (aceitarKeyword(Keyword.CHAR)) {
            return;
        }

        if (aceitarKeyword(Keyword.STRING)) {
            return;
        }

        erroSintatico(
                "esperado um tipo: INTEGER, REAL, CHAR ou STRING."
        );
    }

    // =========================================================
    // CONST
    // =========================================================

    private void declaracaoConstante() {

        if (!aceitarKeyword(Keyword.CONST)) {
            return;
        }

        while (verificar(TokenType.ID)) {

            avancar();

            // CONST ID : tipo = valor ;
            if (aceitar(TokenType.DOIS_PONTOS)) {

                tipo();
            }

            consumir(
                    TokenType.IGUAL,
                    "esperado '=' na declaração de constante."
            );

            valor();

            consumir(
                    TokenType.PONTO_E_VIRGULA,
                    "esperado ';' após declaração de constante."
            );
        }
    }

    // =========================================================
    // PROCEDURE / FUNCTION
    // =========================================================

    private void declProcedimento() {

        while (verificarKeyword(Keyword.PROCEDURE)
                || verificarKeyword(Keyword.FUNCTION)) {

            declProc();
        }
    }

    private void declProc() {

        if (aceitarKeyword(Keyword.PROCEDURE)) {

            consumir(
                    TokenType.ID,
                    "esperado nome da procedure."
            );

            parametros();

            consumir(
                    TokenType.PONTO_E_VIRGULA,
                    "esperado ';' após procedure."
            );

            declaracaoVariavel();

            bloco();

            return;
        }

        if (aceitarKeyword(Keyword.FUNCTION)) {

            consumir(
                    TokenType.ID,
                    "esperado nome da function."
            );

            parametros();

            consumir(
                    TokenType.DOIS_PONTOS,
                    "esperado ':' antes do tipo da function."
            );

            tipo();

            consumir(
                    TokenType.PONTO_E_VIRGULA,
                    "esperado ';' após declaração da function."
            );

            declaracaoVariavel();

            bloco();
        }
    }

    private void parametros() {

        if (!aceitar(TokenType.ABRE_PARENTESES)) {
            return;
        }

        if (!verificar(TokenType.FECHA_PARENTESES)) {

            parametro();

            while (aceitar(TokenType.PONTO_E_VIRGULA)) {

                parametro();
            }
        }

        consumir(
                TokenType.FECHA_PARENTESES,
                "esperado ')'."
        );
    }

    private void parametro() {

        consumir(
                TokenType.ID,
                "esperado identificador no parâmetro."
        );

        while (aceitar(TokenType.VIRGULA)) {

            consumir(
                    TokenType.ID,
                    "esperado identificador após ','."
            );
        }

        consumir(
                TokenType.DOIS_PONTOS,
                "esperado ':' no parâmetro."
        );

        tipo();
    }

    // =========================================================
    // BLOCO
    //
    // bloco → BEGIN instruçoes END ;
    // =========================================================

    private void bloco() {

        consumirKeyword(
                Keyword.BEGIN,
                "esperado 'BEGIN'."
        );

        instrucoes();

        consumirKeyword(
                Keyword.END,
                "esperado 'END'."
        );

        consumir(
                TokenType.PONTO_E_VIRGULA,
                "esperado ';' após bloco."
        );
    }

    // =========================================================
    // INSTRUÇÕES
    // =========================================================

    private void instrucoes() {

        while (tokenAtual != null
                && !verificarKeyword(Keyword.END)
                && !verificarKeyword(Keyword.ELSE)
                && !verificarKeyword(Keyword.UNTIL)) {

            inst();
        }
    }

    // =========================================================
    // INST
    // =========================================================

    private void inst() {

        if (verificar(TokenType.ID)) {

            instrucaoID();

            return;
        }

        if (aceitarKeyword(Keyword.IF)) {

            instrucaoIf();

            return;
        }

        if (aceitarKeyword(Keyword.WHILE)) {

            instrucaoWhile();

            return;
        }

        if (aceitarKeyword(Keyword.REPEAT)) {

            instrucaoRepeat();

            return;
        }

        if (aceitarKeyword(Keyword.FOR)) {

            instrucaoFor();

            return;
        }

        if (aceitarKeyword(Keyword.BREAK)) {

            consumir(
                    TokenType.PONTO_E_VIRGULA,
                    "esperado ';' após BREAK."
            );

            return;
        }

        if (aceitarKeyword(Keyword.CONTINUE)) {

            consumir(
                    TokenType.PONTO_E_VIRGULA,
                    "esperado ';' após CONTINUE."
            );

            return;
        }

        if (verificarKeyword(Keyword.BEGIN)) {

            bloco();

            return;
        }

        erroSintatico(
        "instrução inesperada."
);

sincronizarInstrucao();
    }

    // =========================================================
    // ID
    //
    // ID := expr ;
    // ID [expr] := expr ;
    // ID (parametros2) ;
    // ID ;
    // =========================================================

    private void instrucaoID() {

    Token primeiroID = tokenAtual;

    consumir(
            TokenType.ID,
            "esperado identificador."
    );

    // ID [expr] := expr ;
    if (aceitar(TokenType.ABRE_COLCHETE)) {

        expr();

        consumir(
                TokenType.FECHA_COLCHETE,
                "esperado ']'."
        );

        consumir(
                TokenType.ATRIBUICAO,
                "esperado ':=' após acesso ao vetor."
        );

        expr();

        consumir(
                TokenType.PONTO_E_VIRGULA,
                "esperado ';' após atribuição."
        );

        return;
    }

    // ID (parametros2) ;
    if (aceitar(TokenType.ABRE_PARENTESES)) {

        parametros2();

        consumir(
                TokenType.FECHA_PARENTESES,
                "esperado ')'."
        );

        consumir(
                TokenType.PONTO_E_VIRGULA,
                "esperado ';' após chamada."
        );

        return;
    }

    // ID := expr ;
    if (aceitar(TokenType.ATRIBUICAO)) {

        expr();

        consumir(
                TokenType.PONTO_E_VIRGULA,
                "esperado ';' após atribuição."
        );

        return;
    }

    // ID ;
    if (aceitar(TokenType.PONTO_E_VIRGULA)) {

        warning(
                "a instrução contendo somente um ID não possui efeito nenhum.",
                primeiroID
        );

        return;
    }

    erroSintatico(
            "esperado ':=', '(', '[' ou ';' após identificador."
    );
}


    // =========================================================
    // IF
    // =========================================================

    
private void instrucaoIf() {

    expr();

    consumirKeyword(
            Keyword.THEN,
            "esperado 'THEN' após condição."
    );

    inst();

    if (aceitarKeyword(Keyword.ELSE)) {
        inst();
    }
}



    // =========================================================
    // WHILE
    // =========================================================

    private void instrucaoWhile() {

        expr();

        consumirKeyword(
                Keyword.DO,
                "esperado 'DO' após condição do WHILE."
        );

        inst();
    }

    // =========================================================
    // REPEAT
    // =========================================================

    private void instrucaoRepeat() {

        instrucoes();

        consumirKeyword(
                Keyword.UNTIL,
                "esperado 'UNTIL' após REPEAT."
        );

        expr();

        consumir(
                TokenType.PONTO_E_VIRGULA,
                "esperado ';' após expressão do UNTIL."
        );
    }

    // =========================================================
    // FOR
    //
    // Extensão adicionada:
    //
    // FOR ID := expr TO expr DO inst
    // =========================================================

    private void instrucaoFor() {

        consumir(
                TokenType.ID,
                "esperado identificador após FOR."
        );

        consumir(
                TokenType.ATRIBUICAO,
                "esperado ':=' após identificador do FOR."
        );

        expr();

        consumirKeyword(
                Keyword.TO,
                "esperado 'TO' no FOR."
        );

        expr();

        consumirKeyword(
                Keyword.DO,
                "esperado 'DO' no FOR."
        );

        inst();
    }

    // =========================================================
    // PARAMETROS DE CHAMADA
    // =========================================================

    private void parametros2() {

        if (verificar(TokenType.FECHA_PARENTESES)) {
            return;
        }

        expr();

        while (aceitar(TokenType.VIRGULA)) {

            expr();
        }
    }

    // =========================================================
    // VALOR
    // =========================================================

    private void valor() {

        if (verificar(TokenType.LITERAL)) {

            avancar();

            return;
        }

        unario();
    }

    // =========================================================
    // EXPRESSÕES
    // =========================================================

    private void expr() {

        exprComparacao();

        while (verificarKeyword(Keyword.OU)
                || verificarKeyword(Keyword.E)) {

            avancar();

            exprComparacao();
        }
    }

    private void exprComparacao() {

        exprOp();

        while (verificar(TokenType.IGUAL)
                || verificar(TokenType.DIFERENTE)
                || verificar(TokenType.MENOR)
                || verificar(TokenType.MENOR_IGUAL)
                || verificar(TokenType.MAIOR)
                || verificar(TokenType.MAIOR_IGUAL)) {

            avancar();

            exprOp();
        }
    }

    private void exprOp() {

        termo();

        while (verificar(TokenType.MAIS)
                || verificar(TokenType.MENOS)) {

            avancar();

            termo();
        }
    }

    private void termo() {

        unario();

        while (verificar(TokenType.MULTIPLICACAO)
                || verificar(TokenType.DIVISAO)) {

            avancar();

            unario();
        }
    }

    private void unario() {

        if (aceitar(TokenType.MAIS)) {

            fator();

            return;
        }

        if (aceitar(TokenType.MENOS)) {

            fator();

            return;
        }

        fator();
    }

    // =========================================================
    // FATOR
    // =========================================================

    
private void fator() {

    if (aceitar(TokenType.ABRE_PARENTESES)) {

        expr();

        consumir(
                TokenType.FECHA_PARENTESES,
                "esperado ')'."
        );

        return;
    }

    if (verificar(TokenType.ID)) {

        consumir(
                TokenType.ID,
                "esperado identificador."
        );

        // Chamada de função: ID(expr, expr, ...)
        if (aceitar(TokenType.ABRE_PARENTESES)) {

            parametros2();

            consumir(
                    TokenType.FECHA_PARENTESES,
                    "esperado ')' após argumentos da função."
            );

            return;
        }

        // Acesso a vetor: ID[expr]
        if (aceitar(TokenType.ABRE_COLCHETE)) {

            expr();

            consumir(
                    TokenType.FECHA_COLCHETE,
                    "esperado ']'."
            );
        }

        return;
    }

    if (aceitar(TokenType.NUM)) {
        return;
    }

    if (aceitar(TokenType.LITERAL)) {
        return;
    }

    erroSintatico(
            "esperado expressão, número, literal ou variável."
    );

    if (tokenAtual != null
            && !verificar(TokenType.PONTO_E_VIRGULA)
            && !verificar(TokenType.FECHA_PARENTESES)
            && !verificar(TokenType.FECHA_COLCHETE)
            && !verificar(TokenType.VIRGULA)
            && !verificarKeyword(Keyword.THEN)
            && !verificarKeyword(Keyword.DO)
            && !verificarKeyword(Keyword.END)
            && !verificarKeyword(Keyword.ELSE)
            && !verificarKeyword(Keyword.UNTIL)) {

        avancar();
    }
}


    // =========================================================
    // VARIÁVEL
    //
    // ID
    // ID [expr]
    // =========================================================

    private void variavel() {

        consumir(
                TokenType.ID,
                "esperado identificador."
        );

        if (aceitar(TokenType.ABRE_COLCHETE)) {

            exprOp();

            consumir(
                    TokenType.FECHA_COLCHETE,
                    "esperado ']'."
            );
        }
    }

    // =========================================================
    // RESULTADO
    // =========================================================

    private void imprimirResultado() {

        System.out.println();
        System.out.println("===== ANÁLISE SINTÁTICA =====");

        if (erros.isEmpty()) {

            System.out.println(
                    "Análise sintática concluída sem erros."
            );

        } else {

            for (String erro : erros) {
                System.out.println(erro);
            }
        }

        if (!warnings.isEmpty()) {

            System.out.println();
            System.out.println("===== WARNINGS =====");

            for (String warning : warnings) {
                System.out.println(warning);
            }
        }

        System.out.println("=============================");
    }

    public List<String> getErros() {
        return erros;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
