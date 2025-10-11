package br.com.goibankline.servlet;

import br.com.goibankline.model.Cliente;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * GET /extrato  →  encaminha para templates/extrato.html
 * (nenhum outro verbo; não devolve JSON)
 */
@WebServlet("/extrato")
public class ExtratoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession s = req.getSession();
        Cliente cli   = (Cliente) s.getAttribute("cliente");

        if (cli == null) {                 // não logado → index
            req.getRequestDispatcher("/index.html").forward(req, resp);
            return;
        }

        // forward (não muda URL / refresh SPA‐style)
        req.getRequestDispatcher("/templates/extrato.html")
                .forward(req, resp);
    }
}
