package br.com.goibankline.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet para logout seguro do usuário
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logout(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Invalida toda a sessão (mais seguro)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redireciona para index com mensagem - caminho relativo para AWS CloudFront
        response.sendRedirect("index.html?msg=Logout realizado com sucesso.");
    }
}