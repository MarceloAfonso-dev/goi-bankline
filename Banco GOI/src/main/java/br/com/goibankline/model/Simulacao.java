package br.com.goibankline.model;

public class Simulacao {
    private double rendaMensal;
    private double limite;

    public Simulacao() {}

    public Simulacao(double rendaMensal) {
        this.rendaMensal = rendaMensal;
        this.limite = rendaMensal * 0.2;
    }

    public double getRendaMensal() {
        return rendaMensal;
    }

    public void setRendaMensal(double rendaMensal) {
        this.rendaMensal = rendaMensal;
        this.limite = rendaMensal * 0.2;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

}