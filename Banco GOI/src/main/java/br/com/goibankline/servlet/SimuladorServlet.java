package br.com.goibankline.servlet;

import br.com.goibankline.dao.CpfLiberadoDAO;
import br.com.goibankline.dao.ConnectionFactory;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/api/simulador/cheque")
public class SimuladorServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        /* ----- JSON recebido do front-end ----- */
        br.com.goibankline.model.Simulacao in =
                gson.fromJson(req.getReader(),
                        br.com.goibankline.model.Simulacao.class);

        String  cpf   = in.getCpf();          // só números
        double  renda = in.getRendaMensal();  // já vem como número

        /* ----- consulta a tabela cpfs_liberados ----- */
        boolean liberado;
        try (Connection conn = ConnectionFactory.getConnection()) {
            liberado = new CpfLiberadoDAO(conn).cpfTemNomeLimpo(cpf);
        } catch (SQLException e) {
            throw new ServletException("Erro de banco", e);
        }

        if (!liberado) {                      // 403 – CPF sem limite
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"erro\":\"CPF sem limite disponível\"}");
            return;
        }

        /* ----- CPF OK – calcula limite e devolve 200 OK ----- */
        double limite = renda * 0.20;         // 20 % da renda
        in.setLimite(limite);

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(in));
    }
}
