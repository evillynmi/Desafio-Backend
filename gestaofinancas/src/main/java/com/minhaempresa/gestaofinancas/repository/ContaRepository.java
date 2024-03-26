package com.minhaempresa.gestaofinancas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minhaempresa.gestaofinancas.domain.model.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    List<Conta> findByDataVencimentoAndDescricao(String dataVencimento, String descricao);

    List<Conta> findByDataPagamentoBetween(LocalDate dataInicio, LocalDate dataFim);

    @SuppressWarnings("unchecked")
    Conta save(Conta conta);

    Optional<Conta> findById(Long id);

}