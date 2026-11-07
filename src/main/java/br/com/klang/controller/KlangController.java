
package br.com.klang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.klang.service.KlangService;
import br.com.klang.service.KlangService.ResultadoAnalise;

@Controller
public class KlangController {

    private final KlangService klangService;

    public KlangController(KlangService klangService) {
        this.klangService = klangService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analisar")
    @ResponseBody
    public ResultadoAnalise analisar(
            @RequestParam("codigo") String codigo) {

        return klangService.analisar(codigo);
    }
}
