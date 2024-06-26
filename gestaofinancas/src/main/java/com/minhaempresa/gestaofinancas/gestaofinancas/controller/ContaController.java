package com.minhaempresa.gestaofinancas.gestaofinancas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.Conta;
import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.SituacaoConta;
import com.minhaempresa.gestaofinancas.gestaofinancas.dto.ContaDTO;
import com.minhaempresa.gestaofinancas.gestaofinancas.service.ContaService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    @Autowired
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping("/")
    public String index() {
        return "Bem-vindo à sua aplicação de gestão financeira!";
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Conta> cadastrarConta(@RequestBody ContaDTO conta) {
        Conta novaConta = contaService.cadastrarConta(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conta> atualizarConta(@PathVariable Long id, @RequestBody ContaDTO conta) {
        conta.setId(id);
        Conta contaAtualizada = contaService.atualizarConta(id, conta);
        if (contaAtualizada != null) {
            return ResponseEntity.ok(contaAtualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<Conta> alterarSituacaoConta(@PathVariable Long id, @RequestParam SituacaoConta situacao) {
        Conta contaAtualizada = contaService.alterarSituacaoConta(id, situacao);
        if (contaAtualizada != null) {
            return ResponseEntity.ok(contaAtualizada);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lista")
    public Page<Conta> getContas(Pageable pageable) {
        return contaService.getContas(pageable);
    }


    @PostMapping("/importar-contas")
    public ResponseEntity<String> importarContasViaCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("O arquivo está vazio.");
        }

        try {
            contaService.importarContasViaCSV(file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).body("Contas importadas com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao importar contas.");
        }
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<Conta>> obterContasPorFiltro(
            @RequestParam(required = false) LocalDate dataVencimento,
            @RequestParam(required = false) String descricao,
            Pageable pageable) {
        Page<Conta> contas = contaService.obterContasPorFiltro(dataVencimento, descricao, pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conta> obterContaPorId(@PathVariable Long id) {
        Conta conta = contaService.obterContaPorId(id);
        if (conta != null) {
            return ResponseEntity.ok(conta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/valor-total-pago")
    public ResponseEntity<BigDecimal> obterValorTotalPagoPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        BigDecimal valorTotalPago = contaService.obterValorTotalPagoPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(valorTotalPago);
    }


}
