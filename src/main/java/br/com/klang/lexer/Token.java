package br.com.klang.lexer;

public class Token {

    public TokenType tipo;
    public String lexema;
    public Keyword keyword;

    public int linha;
    public int coluna;

    public Token(TokenType tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    public Token(String lexema, Keyword keyword) {
        this.tipo = TokenType.KEYWORD;
        this.lexema = lexema;
        this.keyword = keyword;
    }

    public Token(TokenType tipo, String lexema, int linha, int coluna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linha = linha;
        this.coluna = coluna;
    }

    public Token(String lexema, Keyword keyword, int linha, int coluna) {
        this.tipo = TokenType.KEYWORD;
        this.lexema = lexema;
        this.keyword = keyword;
        this.linha = linha;
        this.coluna = coluna;
    }

    @Override
    public String toString() {
        return tipo + " -> " + lexema +
                " [" + linha + ":" + coluna + "]";
    }
}
