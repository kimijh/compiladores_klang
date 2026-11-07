package br.com.klang.lexer;

public enum TokenType {

    ID,
    NUM,
    LITERAL,
    KEYWORD,

    MAIS,
    MENOS,
    MULTIPLICACAO,
    DIVISAO,

    IGUAL,
    DIFERENTE,
    MENOR,
    MENOR_IGUAL,
    MAIOR,
    MAIOR_IGUAL,

    ATRIBUICAO,

    PONTO_E_VIRGULA,
    DOIS_PONTOS,
    VIRGULA,

    ABRE_PARENTESES,
    FECHA_PARENTESES,

    ABRE_COLCHETE,
    FECHA_COLCHETE,

    PONTO
}