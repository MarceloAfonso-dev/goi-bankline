package br.com.goibankline.servlet;
import br.com.goibankline.model.Simulacao;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/api/simulador/cheque")
public class SimuladorServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        Simulacao simulacao = gson.fromJson(reader, Simulacao.class);

        // Recalcula o limite com base na renda recebida
        double limiteCalculado = simulacao.getRendaMensal() * 0.2;
        simulacao.setLimite(limiteCalculado);

        String json = gson.toJson(simulacao);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
