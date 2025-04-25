package br.com.goibankline.servlet;

import br.com.goibankline.dao.ContaDAO;
import br.com.goibankline.model.Cliente;
import br.com.goibankline.model.Conta;
import com.google.gson.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/saldo")
public class SaldoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        response.setContentType("application/json; charset=UTF-8");
        JsonObject json = new JsonObject();

        if (cliente == null) {
            json.addProperty("error", "Usuário não logado");
        } else {
            ContaDAO contaDAO = new ContaDAO();
            Conta conta = contaDAO.buscarPorId(cliente.getId());
            if (conta != null) {
                json.addProperty("saldo", conta.getSaldo().toString());
            } else {
                json.addProperty("error", "Conta não encontrada");
            }
        }
        response.getWriter().write(json.toString());
    }
}