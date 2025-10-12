package br.com.goibankline.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet para servir o index.html com limpeza de sessão de segurança
 */
@WebServlet({"/home-redirect"})
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Limpa dados temporários de login se existirem
        if (session != null) {
            session.removeAttribute("clienteTemporario");
            session.removeAttribute("paresAleatorios");
            // NÃO remove cliente/usuarioLogado se estiver autenticado
        }
        
        // Redireciona para index.html com caminho relativo (compatível com AWS CloudFront)
        response.sendRedirect("index.html");
    }
}