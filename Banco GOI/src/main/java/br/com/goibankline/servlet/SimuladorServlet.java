package br.com.goibankline.servlet;

import br.com.goibankline.dao.CpfLiberadoDAO;
import br.com.goibankline.dao.ConnectionFactory;
import br.com.goibankline.model.Simulacao;
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
                          HttpServletResponse resp) throws ServletException, IOException {

        /* ────── 1) Lê e valida o JSON recebido ────── */
        Simulacao in = gson.fromJson(req.getReader(), Simulacao.class);

        if (in == null) {
            enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON mal-formado");
            return;
        }

        String cpfBruto = in.getCpf() == null ? "" : in.getCpf();
        String cpf      = cpfBruto.replaceAll("\\D", "");   // só números
        double renda    = in.getRendaMensal();

        if (cpf.length() != 11 || renda <= 0) {
            enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "CPF ou renda inválidos");
            return;
        }

        /* ────── 2) Verifica se o CPF está liberado ────── */
        boolean liberado;
        try (Connection conn = ConnectionFactory.getConnection()) {
            liberado = new CpfLiberadoDAO(conn).cpfTemNomeLimpo(cpf);
        } catch (SQLException e) {
            throw new ServletException("Erro de banco", e);
        }

        if (!liberado) {
            enviarErro(resp, HttpServletResponse.SC_FORBIDDEN,
                    "CPF sem limite disponível");
            return;
        }

        /* ────── 3) Calcula o limite (20 % da renda) ────── */
        double limite = Math.round(renda * 0.20 * 100) / 100.0; // 2 casas decimais

        /* monta a resposta */
        Simulacao out = new Simulacao();
        out.setCpf(cpf);               // devolve já “limpo”
        out.setRendaMensal(renda);
        out.setLimite(limite);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(out));
    }

    /* utilitário para enviar JSON {"erro": "..."} */
    private void enviarErro(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"erro\":\"" + msg + "\"}");
    }
}
