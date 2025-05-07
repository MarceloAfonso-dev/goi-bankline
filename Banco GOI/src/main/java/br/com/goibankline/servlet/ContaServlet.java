// ContaServlet.java
package br.com.goibankline.servlet;

import br.com.goibankline.dao.ContaDAO;
import br.com.goibankline.model.Cliente;
import br.com.goibankline.model.Conta;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/conta")
public class ContaServlet extends HttpServlet {

    private final ContaDAO contaDAO = new ContaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Cliente cliente = session != null ? (Cliente) session.getAttribute("cliente") : null;
        if (cliente == null) {
            resp.sendError(401, "Usuário não autenticado");
            return;
        }

        // reutiliza buscarPorId com o mesmo ID usado no SaldoServlet
        Conta conta = contaDAO.buscarPorClienteId(cliente.getId());   //  ← aqui

        resp.setContentType("application/json;charset=UTF-8");
        if (conta == null) {
            resp.getWriter().write("{\"agencia\":null,\"conta\":null}");
            return;
        }

        String num = conta.getNumeroConta();         // p.ex. 0001234567
        String ag = num != null && num.length() >= 4 ? num.substring(0, 4) : "";

        class Info {
            String agencia, conta;

            Info(String a, String c) {
                agencia = a;
                conta = c;
            }
        }
        resp.getWriter().write(new Gson().toJson(new Info(ag, num)));
    }
}
