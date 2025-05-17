package br.com.goibankline.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Movimentacao {


    private LocalDate  data;
    private String     descricao;
    private BigDecimal valor;
    private String     tipo;       // Crédito | Débito
    private String     sub;        // Pagamento | Transferência
    private String     icone;

    /* getters / setters */
    public LocalDate  getData()          { return data; }
    public void       setData(LocalDate d){ this.data = d; }

    public String     getDescricao()    { return descricao; }
    public void       setDescricao(String d){ this.descricao = d; }

    public BigDecimal getValor()        { return valor; }
    public void       setValor(BigDecimal v){ this.valor = v; }

    public String     getSub()          { return sub; }
    public void       setSub(String s)  { this.sub = s; }

    public String     getIcone()        { return icone; }
    public void       setIcone(String i){ this.icone = i; }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
