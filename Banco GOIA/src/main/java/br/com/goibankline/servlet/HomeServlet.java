package br.com.goibankline.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet para servir a página home com verificação de autenticação
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Verifica autenticação
        if (session == null || 
            session.getAttribute("usuarioLogado") == null || 
            session.getAttribute("cliente") == null) {
            
            // Não autenticado - redireciona para login com caminho relativo
            response.sendRedirect("index.html?erro=Acesso negado. Faça login primeiro.");
            return;
        }
        
        // Usuário autenticado - serve a página
        request.getRequestDispatcher("/templates/home.html").forward(request, response);
    }
}