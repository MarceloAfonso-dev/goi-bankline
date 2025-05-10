package br.com.goibankline.servlet;

import br.com.goibankline.dao.ClienteDAO;
import br.com.goibankline.model.Cliente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

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
        String nomeCompleto = request.getParameter("nome");
        String cpf = request.getParameter("cpf");
        String dataNascimentoStr = request.getParameter("dataNascimento");
        String email = request.getParameter("email");
        String celular = request.getParameter("celular");
        String cep = request.getParameter("cep");

        if (dataNascimentoStr == null || dataNascimentoStr.isEmpty()) {
            request.setAttribute("errorMessage", "Todos os campos são obrigatórios!");
            request.getRequestDispatcher("/templates/cadastro.html").forward(request, response);
            return;
        }

        if (cep == null || cep.isEmpty()) {
            request.setAttribute("errorMessage", "Todos os campos são obrigatórios!");
            request.getRequestDispatcher("/templates/cadastro.html").forward(request, response);
            return;
        }

        if (nomeCompleto == null || nomeCompleto.isEmpty() ||
                cpf == null || cpf.isEmpty() ||
                email == null || email.isEmpty() ||
                celular == null || celular.isEmpty()) {
            request.setAttribute("errorMessage", "Todos os campos são obrigatórios!");
            request.getRequestDispatcher("/templates/cadastro.html").forward(request, response);
            return;
        }

        String[] partesNome = nomeCompleto.trim().split("\\s+", 2);
        String nome = partesNome[0];
        String sobrenome = partesNome.length > 1 ? partesNome[1] : "";

        LocalDate dataNascimento = LocalDate.parse(dataNascimentoStr);


        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setSobrenome(sobrenome);
        cliente.setCpf(cpf);
        cliente.setDataNascimento(dataNascimento);
        cliente.setEmail(email);
        cliente.setCelular(celular);
        cliente.setCep(cep);

        try {
            ClienteDAO clienteDAO = new ClienteDAO();
            clienteDAO.salvar(cliente); // Salva o cliente no banco de dados
            request.setAttribute("successMessage", "Cadastro realizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erro ao salvar os dados no banco de dados.");
        }

        request.getRequestDispatcher("/templates/cadastro.html").forward(request, response);
    }
}