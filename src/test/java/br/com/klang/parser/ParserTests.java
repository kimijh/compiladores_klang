
package br.com.klang.parser;

import br.com.klang.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTests {

    private Parser criarParser(String codigo) {
        return new Parser(new Lexer(codigo));
    }

    @Test
    void deveAceitarProgramaValido() {
        Parser parser = criarParser(
            "PROGRAM Teste; BEGIN END."
        );

        parser.analisarSemImprimir();

        assertTrue(parser.getErros().isEmpty());
    }

    @Test
    void deveIdentificarBeginAusente() {
        Parser parser = criarParser(
            "PROGRAM Teste; END."
        );

        parser.analisarSemImprimir();

        assertFalse(parser.getErros().isEmpty());
    }

    @Test
    void deveAceitarAtribuicao() {
        Parser parser = criarParser(
            "PROGRAM Teste; BEGIN x := 10; END."
        );

        parser.analisarSemImprimir();

        assertTrue(parser.getErros().isEmpty());
    }

    @Test
    void deveIdentificarExpressaoIncompleta() {
        Parser parser = criarParser(
            "PROGRAM Teste; BEGIN x := ; END."
        );

        parser.analisarSemImprimir();

        assertFalse(parser.getErros().isEmpty());
    }

    @Test
    void deveAceitarIfElse() {
        Parser parser = criarParser(
            "PROGRAM Teste; BEGIN " +
            "IF 10 > 5 THEN x := 1; ELSE x := 2; " +
            "END."
        );

        parser.analisarSemImprimir();

        assertTrue(parser.getErros().isEmpty());
    }

    @Test
    void deveAceitarWhile() {
        Parser parser = criarParser(
            "PROGRAM Teste; BEGIN " +
            "WHILE 10 > 5 DO x := 1; " +
            "END."
        );

        parser.analisarSemImprimir();

        assertTrue(parser.getErros().isEmpty());
    }

    @Test
    void deveAceitarFor() {
        Parser parser = criarParser(
            "PROGRAM Teste; BEGIN " +
            "FOR i := 1 TO 10 DO x := i; " +
            "END."
        );

        parser.analisarSemImprimir();

        assertTrue(parser.getErros().isEmpty());
    }

    @Test
    void deveAceitarFunction() {
        Parser parser = criarParser(
            "PROGRAM Teste; " +
            "FUNCTION Dobro(x: INTEGER): INTEGER; " +
            "BEGIN Dobro := x * 2; END; " +
            "BEGIN y := Dobro(10); END."
        );

        parser.analisarSemImprimir();

        assertTrue(parser.getErros().isEmpty());
    }

    @Test
    void deveAceitarVetor() {
        Parser parser = criarParser(
            "PROGRAM Teste; BEGIN " +
            "vetor[1] := 10; x := vetor[1]; " +
            "END."
        );

        parser.analisarSemImprimir();

        assertTrue(parser.getErros().isEmpty());
    }
}
