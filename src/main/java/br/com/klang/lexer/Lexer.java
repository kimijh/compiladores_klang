package br.com.klang.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

private final String codigo;

private int posicao;
private int linha;
private int coluna;

private final SymbolTable tabelaSimbolos;

private final List<String> erros;

public Lexer(String codigo) {

    this.codigo = codigo;
    this.posicao = 0;
    this.linha = 1;
    this.coluna = 1;

    this.erros = new ArrayList<>();

    this.tabelaSimbolos = new SymbolTable();

    inicializarTabelaSimbolos();
    adicionarSimbolos();
}

private void inicializarTabelaSimbolos() {

    for (Keyword keyword : Keyword.values()) {

        Token token = new Token(
                keyword.name(),
                keyword
        );

        tabelaSimbolos.adicionar(token);
    }
}

public SymbolTable getTabelaSimbolos() {
    return tabelaSimbolos;
}

private void adicionarSimbolos() {

    tabelaSimbolos.adicionar(
            new Token(TokenType.MAIS, "+")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.MENOS, "-")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.MULTIPLICACAO, "*")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.DIVISAO, "/")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.IGUAL, "=")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.DIFERENTE, "<>")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.MENOR, "<")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.MENOR_IGUAL, "<=")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.MAIOR, ">")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.MAIOR_IGUAL, ">=")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.ATRIBUICAO, ":=")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.PONTO_E_VIRGULA, ";")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.DOIS_PONTOS, ":")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.VIRGULA, ",")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.ABRE_PARENTESES, "(")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.FECHA_PARENTESES, ")")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.ABRE_COLCHETE, "[")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.FECHA_COLCHETE, "]")
    );

    tabelaSimbolos.adicionar(
            new Token(TokenType.PONTO, ".")
    );
}

public List<String> getErros() {
    return erros;
}

private void registrarErro(String mensagem) {
    erros.add(mensagem);
}

private char caractereAtual() {
    return codigo.charAt(posicao);
}

private boolean ehLetra(char c) {
    return Character.isLetter(c);
}

private void avancar() {

    if (codigo.charAt(posicao) == '\n') {
        linha++;
        coluna = 1;
    } else {
        coluna++;
    }

    posicao++;
}

private void ignorarEspacos() {

    while (posicao < codigo.length()
            && Character.isWhitespace(caractereAtual())) {

        avancar();
    }
}

public Keyword verificaKeyword(String lexema) {

    for (Keyword k : Keyword.values()) {

        if (k.name().equalsIgnoreCase(lexema)) {
            return k;
        }
    }

    return null;
}

private String lerIdentificador() {

    StringBuilder lexema = new StringBuilder();

    while (posicao < codigo.length()) {

        char c = caractereAtual();

        if (ehLetra(c)
                || Character.isDigit(c)
                || c == '_') {

            lexema.append(c);
            avancar();

        } else {
            break;
        }
    }

    return lexema.toString();
}

private String lerNumero() {

    StringBuilder lexema = new StringBuilder();

    while (posicao < codigo.length()
            && Character.isDigit(caractereAtual())) {

        lexema.append(caractereAtual());
        avancar();
    }

    // Verifica se existe a parte decimal
    if (posicao < codigo.length()
            && caractereAtual() == '.'
            && posicao + 1 < codigo.length()
            && Character.isDigit(codigo.charAt(posicao + 1))) {

        lexema.append(caractereAtual());
        avancar();

        while (posicao < codigo.length()
                && Character.isDigit(caractereAtual())) {

            lexema.append(caractereAtual());
            avancar();
        }
    }

    return lexema.toString();
}

private String lerLiteral() {

    StringBuilder lexema = new StringBuilder();

    // Aspas iniciais
    lexema.append(caractereAtual());
    avancar();

    while (posicao < codigo.length()) {

        char c = caractereAtual();

        if (c == '\'') {

            lexema.append(c);
            avancar();

            return lexema.toString();
        }

        lexema.append(c);
        avancar();
    }

    // Chegou ao final do código sem encontrar
    // a aspa simples de fechamento.
    return null;
}

private boolean ignorarComentario() {

    boolean encontrouComentario = true;

    while (encontrouComentario && posicao < codigo.length()) {

        encontrouComentario = false;

        // Comentário //
        if (posicao + 1 < codigo.length()
                && codigo.charAt(posicao) == '/'
                && codigo.charAt(posicao + 1) == '/') {

            while (posicao < codigo.length()
                    && caractereAtual() != '\n') {

                avancar();
            }

            encontrouComentario = true;
        }

        // Comentário { }
        else if (caractereAtual() == '{') {

            int linhaComentario = linha;
            int colunaComentario = coluna;

            avancar();

            while (posicao < codigo.length()
                    && caractereAtual() != '}') {

                avancar();
            }

            if (posicao < codigo.length()) {

                avancar();

            } else {

                registrarErro(
                "Erro léxico na linha "
                + linhaComentario
                + ", coluna "
                + colunaComentario
                + ": comentário '{' não foi fechado."
                );
            }

            encontrouComentario = true;
        }

        // Comentário (* *)
        else if (posicao + 1 < codigo.length()
                && codigo.charAt(posicao) == '('
                && codigo.charAt(posicao + 1) == '*') {

            int linhaComentario = linha;
            int colunaComentario = coluna;

            avancar();
            avancar();

            while (posicao + 1 < codigo.length()
                    && !(codigo.charAt(posicao) == '*'
                    && codigo.charAt(posicao + 1) == ')')) {

                avancar();
            }

            if (posicao + 1 < codigo.length()) {

                avancar();
                avancar();

            } else {

                registrarErro(
                "Erro léxico na linha "
                + linhaComentario
                + ", coluna "
                + colunaComentario
                + ": comentário '(*' não foi fechado."
                );
            }

            encontrouComentario = true;
        }

        ignorarEspacos();
    }

    return true;
}

private Token lerSimbolo(int linhaToken, int colunaToken) {

    char c = caractereAtual();

    switch (c) {

        case '+':
            avancar();
            return new Token(TokenType.MAIS, "+",
                    linhaToken, colunaToken);

        case '-':
            avancar();
            return new Token(TokenType.MENOS, "-",
                    linhaToken, colunaToken);

        case '*':
            avancar();
            return new Token(TokenType.MULTIPLICACAO, "*",
                    linhaToken, colunaToken);

        case '/':
            avancar();
            return new Token(TokenType.DIVISAO, "/",
                    linhaToken, colunaToken);

        case '=':
            avancar();
            return new Token(TokenType.IGUAL, "=",
                    linhaToken, colunaToken);

        case ':':

            if (posicao + 1 < codigo.length()
                    && codigo.charAt(posicao + 1) == '=') {

                avancar();
                avancar();

                return new Token(TokenType.ATRIBUICAO, ":=",
                        linhaToken, colunaToken);
            }

            avancar();

            return new Token(TokenType.DOIS_PONTOS, ":",
                    linhaToken, colunaToken);

        case '<':

            if (posicao + 1 < codigo.length()) {

                char proximo = codigo.charAt(posicao + 1);

                if (proximo == '=') {

                    avancar();
                    avancar();

                    return new Token(TokenType.MENOR_IGUAL, "<=",
                            linhaToken, colunaToken);
                }

                if (proximo == '>') {

                    avancar();
                    avancar();

                    return new Token(TokenType.DIFERENTE, "<>",
                            linhaToken, colunaToken);
                }
            }

            avancar();

            return new Token(TokenType.MENOR, "<",
                    linhaToken, colunaToken);

        case '>':

            if (posicao + 1 < codigo.length()
                    && codigo.charAt(posicao + 1) == '=') {

                avancar();
                avancar();

                return new Token(TokenType.MAIOR_IGUAL, ">=",
                        linhaToken, colunaToken);
            }

            avancar();

            return new Token(TokenType.MAIOR, ">",
                    linhaToken, colunaToken);

        case ';':
            avancar();
            return new Token(TokenType.PONTO_E_VIRGULA, ";",
                    linhaToken, colunaToken);

        case ',':
            avancar();
            return new Token(TokenType.VIRGULA, ",",
                    linhaToken, colunaToken);

        case '(':
            avancar();
            return new Token(TokenType.ABRE_PARENTESES, "(",
                    linhaToken, colunaToken);

        case ')':
            avancar();
            return new Token(TokenType.FECHA_PARENTESES, ")",
                    linhaToken, colunaToken);

        case '[':
            avancar();
            return new Token(TokenType.ABRE_COLCHETE, "[",
                    linhaToken, colunaToken);

        case ']':
            avancar();
            return new Token(TokenType.FECHA_COLCHETE, "]",
                    linhaToken, colunaToken);

        case '.':
            avancar();
            return new Token(TokenType.PONTO, ".",
                    linhaToken, colunaToken);

        default:
            return null;
    }
}

public Token nextToken() {

    while (true) {

        ignorarEspacos();

        if (posicao >= codigo.length()) {
            return null;
        }

        // Comentários
        if (posicao + 1 < codigo.length()
                && codigo.charAt(posicao) == '/'
                && codigo.charAt(posicao + 1) == '/') {

            ignorarComentario();
            continue;
        }

        if (caractereAtual() == '{') {

            ignorarComentario();
            continue;
        }

        if (posicao + 1 < codigo.length()
                && codigo.charAt(posicao) == '('
                && codigo.charAt(posicao + 1) == '*') {

            ignorarComentario();
            continue;
        }

        int linhaToken = linha;
        int colunaToken = coluna;

        char c = caractereAtual();

        // Identificador ou palavra reservada
        if (ehLetra(c)) {

            String lexema = lerIdentificador();

            // Identificadores podem ter no máximo 15 caracteres
            if (lexema.length() > 15) {

                registrarErro(
                "Erro léxico na linha "
                + linhaToken
                + ", coluna "
                + colunaToken
                + ": identificador '" + lexema
                + "' possui mais de 15 caracteres."
                );

                continue;
            }

            Keyword key = verificaKeyword(lexema);

            if (key != null) {

                return new Token(
                        lexema,
                        key,
                        linhaToken,
                        colunaToken
                );
            }

            Token token = new Token(
                    TokenType.ID,
                    lexema,
                    linhaToken,
                    colunaToken
            );

            tabelaSimbolos.adicionar(token);

            return token;
        }

        // Número
        if (Character.isDigit(c)) {

            String lexema = lerNumero();

            return new Token(
                    TokenType.NUM,
                    lexema,
                    linhaToken,
                    colunaToken
            );
        }

        // Literal
        if (c == '\'') {

            String lexema = lerLiteral();

            if (lexema == null) {

                registrarErro(
        "Erro léxico na linha "
        + linhaToken
        + ", coluna "
        + colunaToken
        + ": literal não foi fechado."
);

                continue;
            }

            return new Token(
                    TokenType.LITERAL,
                    lexema,
                    linhaToken,
                    colunaToken
            );
        }

        // Símbolos
        Token token = lerSimbolo(linhaToken, colunaToken);

        if (token != null) {
            return token;
        }

        // Caractere desconhecido
        avancar();

        registrarErro(
        "Erro léxico na linha "
        + linhaToken
        + ", coluna "
        + colunaToken
        + ": caractere inválido '" + c + "'"
);
    }
}

}