package br.com.goibankline.servlet;

import br.com.goibankline.dao.ContaDAO;
import br.com.goibankline.dao.TransferenciaDAO;
import br.com.goibankline.model.Cliente;
import br.com.goibankline.model.Conta;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/transferencia")
public class TransferenciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtém o cliente logado da sessão
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        // Busca a conta para recuperar o saldo atual
        ContaDAO contaDAO = new ContaDAO();
        Conta conta = contaDAO.buscarPorId(cliente.getId());
        request.setAttribute("saldoAtual", conta.getSaldo());

        // Encaminha para a página de transferência
        request.getRequestDispatcher("/templates/transferencia.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtém o cliente logado como origem
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        // Lê parâmetros do formulário
        String cpfDestino = request.getParameter("cpfDestino");
        String valorStr = request.getParameter("valor");
        BigDecimal valor = new BigDecimal(valorStr);

        TransferenciaDAO transferenciaDAO = new TransferenciaDAO();
        boolean sucesso = transferenciaDAO.transferir(cliente.getCpf(), cpfDestino, valor);

        if (sucesso) {
            request.setAttribute("mensagem", "Transferência realizada com sucesso!");
        } else {
            request.setAttribute("mensagem", "Falha na transferência. Verifique o saldo e os dados informados.");
        }
        doGet(request, response);
    }
}