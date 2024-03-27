package com.minhaempresa.gestaofinancas.gestaofinancas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Page<Conta> findByDataVencimentoAndDescricao(LocalDate dataVencimento, String descricao);

    Page<Conta> findByDataPagamentoBetween(LocalDate dataInicio, LocalDate dataFim);

    // @SuppressWarnings("unchecked")
    // Conta save(Conta conta);

    // Optional<Conta> findById(Long id);

}