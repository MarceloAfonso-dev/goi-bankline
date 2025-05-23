package br.com.goibankline.servlet;

import br.com.goibankline.dao.*;
import br.com.goibankline.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final ClienteDAO clienteDAO         = new ClienteDAO();
    private final ContaDAO   contaDAO           = new ContaDAO();
    private final TransferenciaDAO transfDAO    = new TransferenciaDAO();

    /** utilitário simples p/ gerar conta numérica de 10 dígitos */
    private String gerarNumeroConta() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/templates/cadastro.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        /* ---------- 1) validação básica ---------- */
        String nomeCompleto       = req.getParameter("nome");
        String cpf                = req.getParameter("cpf");
        String dataNascStr        = req.getParameter("dataNascimento");
        String email              = req.getParameter("email");
        String celular            = req.getParameter("celular");
        String cep                = req.getParameter("cep");
        String senha              = req.getParameter("senha");     // novo campo

        if (nomeCompleto.trim().isEmpty() || cpf.trim().isEmpty() || dataNascStr.trim().isEmpty() ||
                email.trim().isEmpty() || celular.trim().isEmpty() || cep.trim().isEmpty() || senha.trim().isEmpty()) {

            req.setAttribute("errorMessage", "Todos os campos são obrigatórios.");
            req.getRequestDispatcher("/templates/cadastro.html").forward(req, resp);
            return;
        }

        /* ---------- 2) quebra nome / cria Cliente ---------- */
        String[] partes = nomeCompleto.trim().split("\\s+", 2);
        Cliente cli = new Cliente();
        cli.setNome(partes[0]);
        cli.setSobrenome(partes.length > 1 ? partes[1] : "");
        cli.setCpf(cpf);
        cli.setDataNascimento(LocalDate.parse(dataNascStr));
        cli.setEmail(email);
        cli.setCelular(celular);
        cli.setCep(cep);

        try {
            int idGerado = clienteDAO.salvarRetornandoId(cli);   // ⬅ altera ClienteDAO p/ devolver a PK
            cli.setId(idGerado);

            /* ---------- 3) cria Conta associada ---------- */
            Conta conta = new Conta();
            conta.setCliente(cli);
            conta.setNumeroConta(gerarNumeroConta());
            conta.setSenha(senha);                               // avalie criptografar
            conta.setSaldo(BigDecimal.ZERO);                     // saldo inicial 0
            conta.setLimiteCredito(BigDecimal.ZERO);
            conta.setDataCriacao(LocalDate.now());

            contaDAO.inserir(conta);

            /* ---------- 4) crédito-bônus ---------- */
            boolean ok = transfDAO.transferir(
                    "66666666666",   // CPF (ou crie um CONST) da conta-origem
                    cpf,
                    new BigDecimal("500")
            );

            if (!ok) {
                req.setAttribute("errorMessage",
                        "Conta criada, mas houve falha ao creditar o bônus. Contate o suporte.");
            } else {
                req.setAttribute("successMessage", "Conta criada com sucesso! R$ 500 já foram creditados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "Erro inesperado no cadastro. Tente novamente.");
        }

        req.getRequestDispatcher("/templates/cadastro.html").forward(req, resp);
    }
}
