
package br.com.klang.lexer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexerTests {

    @Test
    void deveReconhecerIdentificador() {
        Lexer lexer = new Lexer("variavel");

        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(TokenType.ID, token.tipo);
        assertEquals("variavel", token.lexema);
    }

    @Test
    void deveReconhecerPalavraReservada() {
        Lexer lexer = new Lexer("PROGRAM");

        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(TokenType.KEYWORD, token.tipo);
        assertEquals(Keyword.PROGRAM, token.keyword);
    }

    @Test
    void deveReconhecerNumeroInteiro() {
        Lexer lexer = new Lexer("123");

        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(TokenType.NUM, token.tipo);
        assertEquals("123", token.lexema);
    }

    @Test
    void deveReconhecerNumeroReal() {
        Lexer lexer = new Lexer("12.5");

        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(TokenType.NUM, token.tipo);
        assertEquals("12.5", token.lexema);
    }

    @Test
    void deveReconhecerLiteral() {
        Lexer lexer = new Lexer("'Klang'");

        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(TokenType.LITERAL, token.tipo);
        assertEquals("'Klang'", token.lexema);
    }

    @Test
    void deveReconhecerOperadorAtribuicao() {
        Lexer lexer = new Lexer(":=");

        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(TokenType.ATRIBUICAO, token.tipo);
    }

    @Test
    void deveIgnorarComentarioDeLinha() {
        Lexer lexer = new Lexer("// comentario\n123");

        Token token = lexer.nextToken();

        assertNotNull(token);
        assertEquals(TokenType.NUM, token.tipo);
        assertEquals("123", token.lexema);
    }

    @Test
    void deveIdentificarCaractereInvalido() {
        Lexer lexer = new Lexer("@");

        assertNull(lexer.nextToken());
        assertFalse(lexer.getErros().isEmpty());
    }

    @Test
    void deveIdentificarLiteralNaoFechado() {
        Lexer lexer = new Lexer("'Klang");

        assertNull(lexer.nextToken());
        assertFalse(lexer.getErros().isEmpty());
    }
}
