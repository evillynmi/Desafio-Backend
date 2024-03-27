package com.minhaempresa.gestaofinancas.gestaofinancas;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.SituacaoConta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.Conta;
import com.minhaempresa.gestaofinancas.gestaofinancas.dto.ContaDTO;
import com.minhaempresa.gestaofinancas.gestaofinancas.repository.ContaRepository;
import com.minhaempresa.gestaofinancas.gestaofinancas.service.ContaService;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    @Test
    public void testCadastrarConta() {

        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setDataVencimento(LocalDate.now());
        contaDTO.setDataPagamento(LocalDate.now().plusDays(5));
        contaDTO.setValor(BigDecimal.valueOf(100));
        contaDTO.setDescricao("Conta de teste");
        contaDTO.setSituacao(SituacaoConta.PENDENTE);

        Conta contaSalva = new Conta();
        contaSalva.setId(1L);
        contaSalva.setDataVencimento(contaDTO.getDataVencimento());
        contaSalva.setDataPagamento(contaDTO.getDataPagamento());
        contaSalva.setValor(contaDTO.getValor());
        contaSalva.setDescricao(contaDTO.getDescricao());
        contaSalva.setSituacao(contaDTO.getSituacao());

        when(contaRepository.save(any(Conta.class))).thenReturn(contaSalva);


        Conta conta = contaService.cadastrarConta(contaDTO);

        assertThat(conta).isNotNull();
        assertThat(conta.getId()).isEqualTo(1L);
        assertThat(conta.getDataVencimento()).isEqualTo(contaDTO.getDataVencimento());
        assertThat(conta.getDataPagamento()).isEqualTo(contaDTO.getDataPagamento());
        assertThat(conta.getValor()).isEqualTo(contaDTO.getValor());
        assertThat(conta.getDescricao()).isEqualTo(contaDTO.getDescricao());
        assertThat(conta.getSituacao()).isEqualTo(contaDTO.getSituacao());

        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    public void testAtualizarConta() {

        Long idContaExistente = 1L;
        LocalDate novaDataVencimento = LocalDate.now().plusDays(7);
        LocalDate novaDataPagamento = LocalDate.now();
        BigDecimal novoValor = BigDecimal.valueOf(1000.00);
        String novaDescricao = "Nova descrição";
        SituacaoConta novaSituacao = SituacaoConta.PAGA;

        Conta contaExistente = new Conta();
        contaExistente.setId(idContaExistente);
        contaExistente.setDataVencimento(LocalDate.now());
        contaExistente.setDataPagamento(null);
        contaExistente.setValor(BigDecimal.valueOf(500.00));
        contaExistente.setDescricao("Descrição antiga");
        contaExistente.setSituacao(SituacaoConta.PENDENTE);

        ContaDTO novaContaDTO = new ContaDTO();
        novaContaDTO.setDataVencimento(novaDataVencimento);
        novaContaDTO.setDataPagamento(novaDataPagamento);
        novaContaDTO.setValor(novoValor);
        novaContaDTO.setDescricao(novaDescricao);
        novaContaDTO.setSituacao(novaSituacao);

        when(contaRepository.findById(idContaExistente)).thenReturn(Optional.of(contaExistente));
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Conta contaAtualizada = contaService.atualizarConta(idContaExistente, novaContaDTO);

        assertEquals(novaDataVencimento, contaAtualizada.getDataVencimento());
        assertEquals(novaDataPagamento, contaAtualizada.getDataPagamento());
        assertEquals(novoValor, contaAtualizada.getValor());
        assertEquals(novaDescricao, contaAtualizada.getDescricao());
        assertEquals(novaSituacao, contaAtualizada.getSituacao());
    }
}
