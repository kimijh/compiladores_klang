
package br.com.klang.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.klang.lexer.Lexer;
import br.com.klang.parser.Parser;

@Service
public class KlangService {

    public ResultadoAnalise analisar(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            return new ResultadoAnalise(
                false,
                List.of("O código-fonte está vazio."),
                List.of(),
                List.of()
            );
        }

        Lexer lexer = new Lexer(codigo);

        Parser parser = new Parser(lexer);

        parser.analisarSemImprimir();

        List<String> errosLexicos = lexer.getErros();

        List<String> errosSintaticos = parser.getErros();

        List<String> todosErros = new ArrayList<>();

        todosErros.addAll(errosLexicos);
        todosErros.addAll(errosSintaticos);

        List<String> warnings = parser.getWarnings();

        return new ResultadoAnalise(
            todosErros.isEmpty(),
            errosLexicos,
            errosSintaticos,
            warnings
        );
    }

    public record ResultadoAnalise(
        boolean sucesso,
        List<String> errosLexicos,
        List<String> errosSintaticos,
        List<String> warnings
    ) {}
}
