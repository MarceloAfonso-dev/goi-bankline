package br.com.goibankline.servlet;

import br.com.goibankline.dao.ContaDAO;
import br.com.goibankline.model.Cliente;
import br.com.goibankline.model.Conta;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/saldo")
public class SaldoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Recupera cliente da sessão
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        response.setContentType("application/json; charset=UTF-8");
        JsonObject json = new JsonObject();

        if (cliente == null) {
            json.addProperty("error", "Usuário não logado");
        } else {
            ContaDAO contaDAO = new ContaDAO();
            Conta conta = contaDAO.buscarPorClienteId(cliente.getId());   // << aqui

            if (conta != null) {
                // SALDO real
                BigDecimal saldo = conta.getSaldo();
                json.addProperty("saldo", saldo.toPlainString());

                // LIMITE disponível (supondo que exista no seu model; senão, simule)
                //BigDecimal limiteDisp = conta.getLimiteDisponivel() != null
                //        ? conta.getLimiteDisponivel()
                //        : new BigDecimal("300.00"); // valor simulado
                BigDecimal limiteDisp = new BigDecimal("300.00"); // valor simulado
                json.addProperty("limiteDisponivel", limiteDisp.toPlainString());

                // LIMITE no cartão (simulado caso não exista no seu model)
                //BigDecimal limiteCartao = conta.getLimiteCartao() != null
                //        ? conta.getLimiteCartao()
                //        : new BigDecimal("50000.00");
                BigDecimal limiteCartao = new BigDecimal("50000.00");
                json.addProperty("limiteCartao", limiteCartao.toPlainString());

                // VALOR da fatura atual do cartão (simulado)
                //BigDecimal valorFatura = conta.getValorFatura() != null
                //        ? conta.getValorFatura()
                //        : new BigDecimal("300.00");
                BigDecimal valorFatura = new BigDecimal("300.00");
                json.addProperty("valorFatura", valorFatura.toPlainString());

            } else {
                json.addProperty("error", "Conta não encontrada");
            }
        }

        response.getWriter().write(json.toString());
    }
}
