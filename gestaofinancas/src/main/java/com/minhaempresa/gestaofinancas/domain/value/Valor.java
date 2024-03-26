package com.minhaempresa.gestaofinancas.domain.value;

import java.math.BigDecimal;

public class Valor {

    private BigDecimal valor;

    public Valor(BigDecimal valor) {
        this.valor = valor;
    }

    public Valor adicionar(Valor outroValor) {
        return new Valor(this.valor.add(outroValor.valor));
    }

    public Valor subtrair(Valor outroValor) {
        return new Valor(this.valor.subtract(outroValor.valor));
    }

}
