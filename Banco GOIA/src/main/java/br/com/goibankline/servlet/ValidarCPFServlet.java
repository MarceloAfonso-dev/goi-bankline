package br.com.goibankline.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.goibankline.dao.ClienteDAO;
import br.com.goibankline.model.Cliente;

@WebServlet("/validarCPF")
public class ValidarCPFServlet extends HttpServlet {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String cpf      = request.getParameter("cpf");
        String acao     = request.getParameter("acao");

        if ("ajaxVerifica".equals(acao)) {
            // Só verifica existência e retorna JSON
            boolean exists = clienteDAO.buscarPorCPF(cpf) != null;
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter()
                    .write("{\"exists\":" + exists + "}");
            return;
        }

        // fluxo original de login…
        Cliente cliente = clienteDAO.buscarPorCPF(cpf);
        if (cliente != null) {
            // SEGURANÇA: Não colocar cliente na sessão até validar senha
            // Usar atributo temporário para processo de login
            request.getSession().setAttribute("clienteTemporario", cliente);
            request.getSession().removeAttribute("cliente"); // Remove se existir
            request.getRequestDispatcher("/login").forward(request, response);
        } else {
            request.getRequestDispatcher("/index.html?erro=CPF não encontrado.").forward(request, response);
        }
    }
}

