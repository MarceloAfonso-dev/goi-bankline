package br.com.goibankline.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filtro de autenticação para proteger páginas que requerem login
 */
@WebFilter(urlPatterns = {
    "/templates/home.html",
    "/templates/extrato.html", 
    "/templates/transferencia.html",
    "/home",
    "/extrato",
    "/transferencia"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        // Verifica se usuário está autenticado
        boolean isLoggedIn = false;
        if (session != null) {
            Boolean loggedIn = (Boolean) session.getAttribute("usuarioLogado");
            Object cliente = session.getAttribute("cliente");
            isLoggedIn = (loggedIn != null && loggedIn && cliente != null);
        }
        
        if (isLoggedIn) {
            // Usuário autenticado - permite acesso
            chain.doFilter(request, response);
        } else {
            // Usuário não autenticado - redireciona para index
            String contextPath = httpRequest.getContextPath();
            httpResponse.sendRedirect(contextPath + "/index.html?erro=Sessão expirada. Faça login novamente.");
        }
    }

    @Override
    public void destroy() {
        // Cleanup do filtro
    }
}