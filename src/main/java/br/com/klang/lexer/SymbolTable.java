package br.com.klang.lexer;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<String, Token> tabela;

    public SymbolTable() {
        tabela = new HashMap<>();
    }

    public void adicionar(Token token) {

        String chave = token.lexema.toLowerCase();

        tabela.put(chave, token);
    }

    public Token buscar(String lexema) {

        String chave = lexema.toLowerCase();

        return tabela.get(chave);
    }

    public boolean contem(String lexema) {

        String chave = lexema.toLowerCase();

        return tabela.containsKey(chave);
    }

    public void imprimir() {

        System.out.println();
        System.out.println("===== TABELA DE SÍMBOLOS =====");

        for (Token token : tabela.values()) {

            System.out.println(
                    token.tipo
                    + " -> "
                    + token.lexema
            );
        }

        System.out.println("===============================");
    }
}
