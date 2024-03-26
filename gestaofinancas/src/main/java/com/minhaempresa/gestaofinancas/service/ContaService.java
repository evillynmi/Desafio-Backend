package com.minhaempresa.gestaofinancas.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhaempresa.gestaofinancas.domain.model.Conta;
import com.minhaempresa.gestaofinancas.domain.model.SituacaoConta;
import com.minhaempresa.gestaofinancas.dto.ContaDTO;
import com.minhaempresa.gestaofinancas.repository.ContaRepository;

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
        // Verificar se a conta com o ID fornecido existe no banco de dados
        Optional<Conta> optionalConta = contaRepository.findById(id);
        if (optionalConta.isPresent()) {
            Conta contaExistente = optionalConta.get();
            // Atualizar os campos da conta com base nos dados recebidos
            contaExistente.setDataVencimento(contaDTO.getDataVencimento());
            contaExistente.setDataPagamento(contaDTO.getDataPagamento());
            contaExistente.setValor(contaDTO.getValor());
            contaExistente.setDescricao(contaDTO.getDescricao());
            contaExistente.setSituacao(contaDTO.getSituacao());
            // Salvar a conta atualizada no banco de dados
            return contaRepository.save(contaExistente);
        }
        return null; // Se a conta não existir, retorne null ou trate de acordo com sua lógica
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

    // Método para obter uma lista de contas com filtro de data de vencimento e
    // descrição
    public List<Conta> obterContasPorFiltro(String dataVencimento, String descricao) {
        return contaRepository.findByDataVencimentoAndDescricao(dataVencimento, descricao);
    }

    // Método para obter uma conta pelo ID
    public Conta obterContaPorId(Long id) {
        return contaRepository.findById(id).orElse(null);
    }

    public BigDecimal obterValorTotalPagoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal valorTotalPago = BigDecimal.ZERO;

        // Consulta as contas pagas dentro do período especificado
        List<Conta> contasPagas = contaRepository.findByDataPagamentoBetween(dataInicio, dataFim);

        // Soma os valores das contas pagas
        for (Conta conta : contasPagas) {
            valorTotalPago = valorTotalPago.add(conta.getValor());
        }

        return valorTotalPago;
    }

    public void importarContasViaCSV(String caminhoArquivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dadosConta = linha.split(",");
                // Extrair os dados da linha
                LocalDate dataVencimento = LocalDate.parse(dadosConta[0]); // Supondo que a data esteja na primeira
                                                                           // coluna
                LocalDate dataPagamento = LocalDate.parse(dadosConta[1]); // Supondo que a data esteja na segunda coluna
                BigDecimal valor = new BigDecimal(dadosConta[2]); // Supondo que o valor esteja na terceira coluna
                String descricao = dadosConta[3]; // Supondo que a descrição esteja na quarta coluna
                SituacaoConta situacao = SituacaoConta.valueOf(dadosConta[4]); // Supondo que a situação esteja na
                                                                               // quinta coluna

                // Criar uma nova instância de Conta
                Conta conta = new Conta();
                conta.setDataVencimento(dataVencimento);
                conta.setDataPagamento(dataPagamento);
                conta.setValor(valor);
                conta.setDescricao(descricao);
                conta.setSituacao(situacao);

                // Salvar a conta no banco de dados
                contaRepository.save(conta);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Tratar exceção conforme necessário
        }
    }
}
