package br.com.goibankline.servlet;

import br.com.goibankline.dao.ClienteDAO;
import br.com.goibankline.model.Cliente;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

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
            request.getSession().setAttribute("cliente", cliente);
            request.getRequestDispatcher("/login").forward(request, response);
        } else {
            request.getRequestDispatcher("/index.html?erro=CPF não encontrado.").forward(request, response);
        }
    }
}

