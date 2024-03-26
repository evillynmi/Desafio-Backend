package com.minhaempresa.gestaofinancas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minhaempresa.gestaofinancas.domain.model.Conta;
import com.minhaempresa.gestaofinancas.domain.model.SituacaoConta;
import com.minhaempresa.gestaofinancas.dto.ContaDTO;
import com.minhaempresa.gestaofinancas.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    @Autowired
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
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

    // Implementar os outros métodos do CRUD e outras operações conforme necessário
}