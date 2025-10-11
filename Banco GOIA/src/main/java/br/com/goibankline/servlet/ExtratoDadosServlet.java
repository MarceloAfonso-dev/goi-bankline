package br.com.goibankline.servlet;

import br.com.goibankline.dao.ContaDAO;
import br.com.goibankline.dao.MovimentacaoDAO;
import br.com.goibankline.model.Cliente;
import br.com.goibankline.model.Conta;
import br.com.goibankline.model.Movimentacao;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * GET /extratoDados  →  JSON com as movimentações do cliente logado
 */
@WebServlet("/extratoDados")
public class ExtratoDadosServlet extends HttpServlet {

    private final ContaDAO        contaDAO = new ContaDAO();
    private final MovimentacaoDAO movDAO   = new MovimentacaoDAO();
    private final Gson            gson     = new Gson();
    private final DateTimeFormatter ISO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession sess = req.getSession(false);
        Cliente cli      = (sess != null) ? (Cliente) sess.getAttribute("cliente") : null;
        if (cli == null) { resp.getWriter().write("[]"); return; }

        Conta conta = contaDAO.buscarPorClienteId(cli.getId());
        if (conta == null) { resp.getWriter().write("[]"); return; }

        List<Movimentacao> lista = movDAO.listarPorConta(conta.getIdConta());

        /* ---- monta JSON enxuto ---- */
        List<Map<String,Object>> out = new ArrayList<>();
        for (Movimentacao m : lista) {
            Map<String,Object> o = new HashMap<>();
            o.put("data", m.getData().format(ISO));  // corrigido de "dataMovimento" para "data"
            o.put("descricao", m.getDescricao());
            o.put("valor"    , m.getValor());      // BigDecimal vira número
            o.put("tipo"     , m.getTipo());       // opcional no front
            o.put("sub"      , m.getSub());
            o.put("icone"    , m.getIcone());
            out.add(o);
        }
        resp.getWriter().write(gson.toJson(out));
    }
}
