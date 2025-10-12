package br.com.goibankline.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/erro")
public class ErroServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ErroServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Capturar informações do erro
        String codigoErro = req.getParameter("codigo");
        String mensagemErro = req.getParameter("msg");
        String origem = req.getParameter("origem");
        
        // Log do erro para análise posterior
        logger.log(Level.WARNING, 
            "Página de erro acessada - Código: {0}, Mensagem: {1}, Origem: {2}, IP: {3}", 
            new Object[]{codigoErro, mensagemErro, origem, req.getRemoteAddr()});
        
        // Definir atributos para a página
        if (codigoErro != null) {
            req.setAttribute("codigoErro", codigoErro);
        }
        if (mensagemErro != null) {
            req.setAttribute("mensagemErro", mensagemErro);
        }
        
        // Encaminhar para a página de erro
        req.getRequestDispatcher("/templates/erro.html").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // POST também redireciona para GET
        doGet(req, resp);
    }
}