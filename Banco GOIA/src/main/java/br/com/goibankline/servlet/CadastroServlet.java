package br.com.goibankline.servlet;

import br.com.goibankline.dao.ClienteDAO;
import br.com.goibankline.dao.ContaDAO;
import br.com.goibankline.dao.TransferenciaDAO;
import br.com.goibankline.model.Cliente;
import br.com.goibankline.model.Conta;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final ClienteDAO      clienteDAO = new ClienteDAO();
    private final ContaDAO        contaDAO   = new ContaDAO();
    private final TransferenciaDAO transfDAO = new TransferenciaDAO();

    /* gera um número de 10 dígitos (não-formatado) para a nova conta */
    private String gerarNumeroConta() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    /* ---------- GET: devolve o formulário ---------- */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/templates/cadastro.html").forward(req, resp);
    }

    /* ---------- POST: processa o cadastro ---------- */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        /* ---------- 1) leitura / validação simples ---------- */
        String nomeCompleto = req.getParameter("nome");
        String cpf          = req.getParameter("cpf");
        String dataNascStr  = req.getParameter("dataNascimento");
        String email        = req.getParameter("email");
        String celular      = req.getParameter("celular");
        String cep          = req.getParameter("cep");
        String senha        = req.getParameter("senha");

        if (nomeCompleto == null || cpf == null || dataNascStr == null ||
                email == null       || celular == null || cep == null || senha == null ||
                nomeCompleto.trim().isEmpty() || cpf.trim().isEmpty() || dataNascStr.trim().isEmpty() ||
                email.trim().isEmpty() || celular.trim().isEmpty() || cep.trim().isEmpty() || senha.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Todos os campos são obrigatórios.");
            req.getRequestDispatcher("/templates/cadastro.html").forward(req, resp);
            return;
        }

        /* ---------- 2) monta objeto Cliente ---------- */
        String[] partes = nomeCompleto.trim().split("\\s+", 2);

        Cliente cli = new Cliente();
        cli.setNome(partes[0]);
        cli.setSobrenome(partes.length > 1 ? partes[1] : "");
        cli.setCpf(cpf);
        cli.setDataNascimento(LocalDate.parse(dataNascStr));
        cli.setEmail(email);
        cli.setCelular(celular);
        cli.setCep(cep);

        boolean okCadastro = false;  // flag para decidir o redirect

        try {
            /* ---------- 3) grava Cliente e Conta ---------- */
            int idGerado = clienteDAO.salvarRetornandoId(cli);   // devolve PK gerada
            cli.setId(idGerado);

            Conta conta = new Conta();
            conta.setCliente(cli);
            conta.setNumeroConta(gerarNumeroConta());
            conta.setSenha(senha);                    // considere hashear
            conta.setSaldo(new BigDecimal("1500"));   // SALDO INICIAL DE R$ 1.500 (BÔNUS ÚNICO)
            conta.setLimiteCredito(BigDecimal.ZERO);
            conta.setDataCriacao(LocalDate.now());

            contaDAO.inserir(conta);
            okCadastro = true;  // Conta criada com sucesso com bônus de R$ 1.500

        } catch (Exception e) {        // SQL ou validações
            e.printStackTrace();
            req.setAttribute("errorMessage",
                    "Erro inesperado no cadastro. Tente novamente.");
        }

        /* ---------- 5) decide resposta ---------- */
        if (okCadastro) {
            /* forward p/ GET /cadastro?sucesso=1 para manter o domínio –
               o JS do front lê esse parâmetro e exibe o pop-up */
            req.getRequestDispatcher("/cadastro?sucesso=1").forward(req, resp);
        } else {
            /* houve algum erro → volta para o formulário exibindo mensagens */
            req.getRequestDispatcher("/templates/cadastro.html").forward(req, resp);
        }
    }
}
