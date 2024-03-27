package com.minhaempresa.gestaofinancas.gestaofinancas.repository;

import com.minhaempresa.gestaofinancas.gestaofinancas.domain.model.Conta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Page<Conta> findByDataVencimentoAndDescricao(LocalDate dataVencimento, String descricao, Pageable pageable);

    Page<Conta> findByDataPagamentoBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

}