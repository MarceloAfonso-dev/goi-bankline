package br.com.goibankline.model;

public class Simulacao {

    private String cpf;          // 11 dígitos, só números
    private double rendaMensal;
    private double limite;       // calculado pelo servlet

    public Simulacao() {}        // preciso para o Gson

    public String  getCpf()                 { return cpf;          }
    public void    setCpf(String cpf)       { this.cpf = cpf;      }

    public double  getRendaMensal()         { return rendaMensal;  }
    public void    setRendaMensal(double r) { this.rendaMensal = r;}

    public double  getLimite()              { return limite;       }
    public void    setLimite(double limite) { this.limite = limite;}
}
