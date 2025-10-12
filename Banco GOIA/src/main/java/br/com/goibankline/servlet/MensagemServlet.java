package br.com.goibankline.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/mensagem")
public class MensagemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String mensagem = (String) session.getAttribute("mensagemTransferencia");

        PrintWriter out = response.getWriter();

        if (mensagem != null) {
            // Remove a mensagem da sessão após buscar
            session.removeAttribute("mensagemTransferencia");

            // Retorna a mensagem como JSON
            out.print("{\"mensagem\":\"" + mensagem.replace("\"", "\\\"") + "\"}");
        } else {
            out.print("{\"mensagem\":null}");
        }

        out.flush();
    }
}
