package com.minhaempresa.gestaofinancas.gestaofinancas.service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.Conta;
import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.SituacaoConta;
import com.minhaempresa.gestaofinancas.gestaofinancas.dto.ContaDTO;
import com.minhaempresa.gestaofinancas.gestaofinancas.repository.ContaRepository;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    @Autowired
    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public Conta cadastrarConta(ContaDTO contaDTO) {
        Conta conta = new Conta();
        conta.setDataVencimento(contaDTO.getDataVencimento());
        conta.setDataPagamento(contaDTO.getDataPagamento());
        conta.setValor(contaDTO.getValor());
        conta.setDescricao(contaDTO.getDescricao());
        conta.setSituacao(contaDTO.getSituacao());

        return contaRepository.save(conta);
    }

    public Conta atualizarConta(Long id, ContaDTO contaDTO) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta contaExistente = optionalConta.get();
            contaExistente.setDataVencimento(contaDTO.getDataVencimento());
            contaExistente.setDataPagamento(contaDTO.getDataPagamento());
            contaExistente.setValor(contaDTO.getValor());
            contaExistente.setDescricao(contaDTO.getDescricao());
            contaExistente.setSituacao(contaDTO.getSituacao());
            return contaRepository.save(contaExistente);
        }
        return null;
    }

    public Conta alterarSituacaoConta(Long id, SituacaoConta situacao) {
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta conta = optionalConta.get();
            conta.setSituacao(situacao);
            return contaRepository.save(conta);
        } else {
            // throw new ContaNaoEncontradaException("Conta não encontrada com o ID: " +
            // id);
        }
        return null;
    }

    public List<Conta> obterContasPorFiltro(LocalDate dataVencimento, String descricao) {
        return contaRepository.findByDataVencimentoAndDescricao(dataVencimento, descricao);
    }

    public Conta obterContaPorId(Long id) {
        return contaRepository.findById(id).orElse(null);
    }

    public BigDecimal obterValorTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal valorTotalPago = BigDecimal.ZERO;

        List<Conta> contasPagas = contaRepository.findByDataPagamentoBetween(dataInicio, dataFim);

        for (Conta conta : contasPagas) {
            valorTotalPago = valorTotalPago.add(conta.getValor());
        }

        return valorTotalPago;
    }

    public void importarContasViaCSV(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dadosConta = linha.split(",");

                LocalDate dataVencimento = LocalDate.parse(dadosConta[0]);
                LocalDate dataPagamento = LocalDate.parse(dadosConta[1]);
                BigDecimal valor = new BigDecimal(dadosConta[2]);
                String descricao = dadosConta[3];
                SituacaoConta situacao = SituacaoConta.valueOf(dadosConta[4]);

                Conta conta = new Conta();
                conta.setDataVencimento(dataVencimento);
                conta.setDataPagamento(dataPagamento);
                conta.setValor(valor);
                conta.setDescricao(descricao);
                conta.setSituacao(situacao);

                contaRepository.save(conta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Page<Conta> getContas(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }
}
