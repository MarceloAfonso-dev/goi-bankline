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
import java.net.URLEncoder;
import java.util.List;

@WebServlet("/transferencia")
public class TransferenciaServlet extends HttpServlet {

    private final ContaDAO contaDAO           = new ContaDAO();
    private final ClienteDAO clienteDAO       = new ClienteDAO();
    private final TransferenciaDAO txDAO      = new TransferenciaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cliente c = (Cliente) session.getAttribute("cliente");
        if (c == null) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }
        // encaminha para o HTML estático
        request.getRequestDispatcher("/templates/transferencia.html")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Cliente clienteOrigem = (Cliente) session.getAttribute("cliente");
        if (clienteOrigem == null) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        String cpfDestino = request.getParameter("cpfDestino");
        String valorStr   = request.getParameter("valor");

        // 1) Não pode transferir para si mesmo
        if (cpfDestino.equals(clienteOrigem.getCpf())) {
            redirectWithMsg(request, response, "Não é possível transferir para a própria conta.");
            return;
        }

        // 2) Normaliza e parseia o valor
        String norm = valorStr.replaceAll("\\.", "").replace(',', '.');
        BigDecimal valor;
        try {
            valor = new BigDecimal(norm);
        } catch (NumberFormatException e) {
            redirectWithMsg(request, response, "Valor inválido.");
            return;
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            redirectWithMsg(request, response, "Informe um valor maior que zero.");
            return;
        }

        // 3) Busca conta origem
        Conta contaOrigem = findContaPorCliente(clienteOrigem.getId());
        if (contaOrigem == null) {
            redirectWithMsg(request, response, "Conta de origem não encontrada.");
            return;
        }
        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            redirectWithMsg(request, response, "Saldo insuficiente.");
            return;
        }

        // 4) Verifica cliente destino existe
        Cliente clienteDestino = clienteDAO.buscarPorCPF(cpfDestino);
        if (clienteDestino == null) {
            redirectWithMsg(request, response, "CPF de destino não encontrado.");
            return;
        }

        // 5) Verifica conta destino existe
        Conta contaDestino = findContaPorCliente(clienteDestino.getId());
        if (contaDestino == null) {
            redirectWithMsg(request, response, "Conta de destino não encontrada.");
            return;
        }

        // 6) Executa a transferência
        boolean sucesso = txDAO.transferir(
                clienteOrigem.getCpf(),
                cpfDestino,
                valor
        );

        redirectWithMsg(request, response,
                sucesso
                        ? "Transferência realizada com sucesso!"
                        : "Falha ao processar transferência."
        );
    }

    private void redirectWithMsg(HttpServletRequest req,
                                 HttpServletResponse resp,
                                 String msg) throws IOException, ServletException {
        // Forward para a página com mensagem como query parameter
        String qs = "?msg=" + URLEncoder.encode(msg, "UTF-8");
        req.getRequestDispatcher("/transferencia" + qs).forward(req, resp);
    }

    private Conta findContaPorCliente(int idCliente) {
        List<Conta> contas = contaDAO.listarTodos();
        for (Conta c : contas) {
            if (c.getCliente() != null && c.getCliente().getId() == idCliente) {
                return c;
            }
        }
        return null;
    }
}
