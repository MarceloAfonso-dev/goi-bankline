package br.com.goibankline.servlet;

import br.com.goibankline.dao.ContaDAO;
import br.com.goibankline.model.Cliente;
import br.com.goibankline.model.Conta;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GET /conta  →  {"agencia":"0001","conta":"0001234567"}
 * Sempre devolve JSON; nunca quebra o front-end.
 */
@WebServlet("/conta")
public class ContaServlet extends HttpServlet {

    private final ContaDAO dao = new ContaDAO();
    private final Gson gson   = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("[SERVLET] /conta  INICIO");

        HttpSession s  = req.getSession(false);
        Cliente cliente = (s != null) ? (Cliente) s.getAttribute("cliente") : null;

        if (cliente == null) {
            System.out.println("[SERVLET] usuário NÃO autenticado");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não autenticado");
            return;
        }
        System.out.println("[SERVLET] cliente.id = " + cliente.getId());

        Conta conta = dao.buscarPorClienteId(cliente.getId());

        resp.setContentType("application/json;charset=UTF-8");

        /* --- monta JSON com HashMap para evitar qualquer serialização nula --- */
        Map<String, Object> out = new HashMap<>();

        if (conta == null) {
            out.put("agencia", null);
            out.put("conta"  , null);
            /* novo campo */
            out.put("nome"   , null);
        } else {
            String num = conta.getNumeroConta();      // 548123
            String ag  = (num != null && num.length() >= 4) ? num.substring(0, 4) : "";
            out.put("agencia", ag);
            out.put("conta"  , num);

            /* ===== novo campo “nome” ===== */
            Cliente cli = cliente;                    // já temos o cliente logado
            out.put("nome", cli != null ? cli.getNome() : null);
        }

        String json = gson.toJson(out);
        resp.getWriter().write(json);
        System.out.println("[SERVLET] JSON enviado = " + json);
    }
}
