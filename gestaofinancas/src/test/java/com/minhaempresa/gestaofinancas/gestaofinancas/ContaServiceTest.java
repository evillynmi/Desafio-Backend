package com.minhaempresa.gestaofinancas.gestaofinancas;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.Conta;
import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.SituacaoConta;
import com.minhaempresa.gestaofinancas.gestaofinancas.dto.ContaDTO;
import com.minhaempresa.gestaofinancas.gestaofinancas.exception.ContaNaoEncontradaException;
import com.minhaempresa.gestaofinancas.gestaofinancas.repository.ContaRepository;
import com.minhaempresa.gestaofinancas.gestaofinancas.service.ContaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    public void testAlterarSituacaoConta() {

        Long idContaExistente = 1L;
        SituacaoConta novaSituacao = SituacaoConta.PAGA;

        Conta contaExistente = new Conta();
        contaExistente.setId(idContaExistente);
        contaExistente.setSituacao(SituacaoConta.PENDENTE);

        when(contaRepository.findById(idContaExistente)).thenReturn(Optional.of(contaExistente));
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Conta contaAtualizada = contaService.alterarSituacaoConta(idContaExistente, novaSituacao);

        assertEquals(novaSituacao, contaAtualizada.getSituacao());
    }

    @Test
    public void testObterContasPorFiltro() {

        LocalDate dataVencimento = LocalDate.of(2024, 3, 25);
        String descricao = "Conta de teste";

        Conta contaTeste = new Conta();
        contaTeste.setId(1L);
        contaTeste.setDataVencimento(dataVencimento);
        contaTeste.setDataPagamento(dataVencimento.plusDays(1));
        contaTeste.setValor(BigDecimal.valueOf(100.00));
        contaTeste.setDescricao(descricao);
        contaTeste.setSituacao(SituacaoConta.PENDENTE);

        List<Conta> contasEsperadas = new ArrayList<>();
        contasEsperadas.add(contaTeste);

        Page<Conta> contasPage = new PageImpl<>(contasEsperadas);

        when(contaRepository.findByDataVencimentoAndDescricao(eq(dataVencimento), eq(descricao), Pageable.unpaged()))
                .thenReturn(contasPage);

        ContaService contaService = new ContaService(contaRepository);

        Page<Conta> contasRetornadas = contaService.obterContasPorFiltro(dataVencimento, descricao, Pageable.unpaged());

        assertEquals(contasEsperadas, contasRetornadas.getContent());

        verify(contaRepository, times(1)).findByDataVencimentoAndDescricao(eq(dataVencimento), eq(descricao),Pageable.unpaged());
    }


    @Test
    public void TestObterContaPorId() {

        when(contaRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ContaNaoEncontradaException.class, () -> contaService.obterContaPorId(1L));
    }

    @Test
    public void testObterValorTotalPagoPorPeriodo() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);

        Conta conta1 = new Conta();
        conta1.setValor(new BigDecimal("100.00"));

        Conta conta2 = new Conta();
        conta2.setValor(new BigDecimal("200.00"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Conta> page = new PageImpl<>(Arrays.asList(conta1, conta2), pageable, 2);

        when(contaRepository.findByDataPagamentoBetween(dataInicio, dataFim, pageable)).thenReturn(page);

        BigDecimal valorTotal = contaService.obterValorTotalPagoPorPeriodo(dataInicio, dataFim);

        assertEquals(new BigDecimal("300.00"), valorTotal);
    }

    @Test
    public void testImportarContasViaCSV() {
        String csvData = "2024-03-01,2024-03-10,100.00,Conta 1,PAGA\n"
                + "2024-03-05,2024-03-12,150.00,Conta 2,PENDENTE\n"
                + "2024-03-10,2024-03-20,200.00,Conta 3,PAGA\n";

        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());

        contaService.importarContasViaCSV(inputStream);

        verify(contaRepository, times(3)).save(any(Conta.class));
    }

    @Test
    public void testGetContas() {

        List<Conta> contas = new ArrayList<>();
        contas.add(new Conta());
        contas.add(new Conta());
        contas.add(new Conta());

        Page<Conta> paginaContas = new PageImpl<>(contas);

        when(contaRepository.findAll(any(Pageable.class))).thenReturn(paginaContas);

        Page<Conta> resultado = contaService.getContas(Pageable.unpaged());

        assertSame(paginaContas, resultado);
        assertEquals(contas.size(), resultado.getContent().size());
    }
}
