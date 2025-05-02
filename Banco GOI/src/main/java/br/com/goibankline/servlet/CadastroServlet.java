package br.com.goibankline.servlet;

import br.com.goibankline.model.Cliente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/templates/cadastro.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String cpf = request.getParameter("cpf");
        String email = request.getParameter("email");
        String celular = request.getParameter("celular");


        if (nome == null || nome.isEmpty() ||
                cpf == null || cpf.isEmpty() ||
                email == null || email.isEmpty() ||
                celular == null || celular.isEmpty()) {
            request.setAttribute("errorMessage", "Todos os campos são obrigatórios!");
            request.getRequestDispatcher("/templates/cadastro.html").forward(request, response);
            return;
        }


        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setEmail(email);
        cliente.setCelular(celular);


        HttpSession session = request.getSession();
        session.setAttribute("cliente", cliente);


        request.setAttribute("successMessage", "Cadastro realizado com sucesso!");
        request.getRequestDispatcher("/templates/cadastro.html").forward(request, response);
    }
}