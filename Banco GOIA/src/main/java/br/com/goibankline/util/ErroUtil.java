package br.com.goibankline.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

/**
 * Utilitário para gerenciar redirecionamentos para página de erro
 */
public class ErroUtil {
    
    private static final Logger logger = Logger.getLogger(ErroUtil.class.getName());
    private static final String ERRO_URL = "/erro";
    
    /**
     * Redireciona para página de erro com código e mensagem
     */
    public static void redirecionarParaErro(HttpServletResponse response, 
                                          String codigo, String mensagem) 
            throws IOException {
        
        try {
            String mensagemCodificada = URLEncoder.encode(mensagem, "UTF-8");
            String url = ERRO_URL + "?codigo=" + codigo + "&msg=" + mensagemCodificada;
            
            logger.log(Level.WARNING, "Redirecionando para página de erro: {0} - {1}", 
                      new Object[]{codigo, mensagem});
            
            response.sendRedirect(url);
        } catch (UnsupportedEncodingException e) {
            // UTF-8 sempre deve estar disponível, mas como fallback:
            String url = ERRO_URL + "?codigo=" + codigo;
            logger.log(Level.WARNING, "Erro ao codificar mensagem, usando URL sem mensagem: {0}", codigo);
            response.sendRedirect(url);
        }
    }
    
    /**
     * Redireciona para página de erro apenas com código
     */
    public static void redirecionarParaErro(HttpServletResponse response, String codigo) 
            throws IOException {
        
        String mensagem = obterMensagemPadrao(codigo);
        redirecionarParaErro(response, codigo, mensagem);
    }
    
    /**
     * Redireciona para página de erro com exception
     */
    public static void redirecionarParaErro(HttpServletResponse response, 
                                          String codigo, Exception e) 
            throws IOException {
        
        String mensagem = "Erro no sistema: " + e.getMessage();
        logger.log(Level.SEVERE, "Erro capturado e redirecionado", e);
        redirecionarParaErro(response, codigo, mensagem);
    }
    
    /**
     * Redireciona para erro genérico
     */
    public static void redirecionarParaErroGenerico(HttpServletResponse response) 
            throws IOException {
        
        redirecionarParaErro(response, "GERAL", "Algo deu errado. Tente novamente.");
    }
    
    /**
     * Obtém mensagem padrão para códigos conhecidos
     */
    private static String obterMensagemPadrao(String codigo) {
        switch (codigo) {
            case "404":
                return "Página não encontrada";
            case "500":
                return "Erro interno do servidor";
            case "403":
                return "Acesso negado";
            case "400":
                return "Requisição inválida";
            case "TIMEOUT":
                return "Tempo limite da operação excedido";
            case "DB_ERROR":
                return "Erro de conexão com banco de dados";
            case "VALIDATION":
                return "Dados fornecidos são inválidos";
            case "AUTH":
                return "Falha na autenticação";
            default:
                return "Erro inesperado no sistema";
        }
    }
    
    /**
     * Redireciona de forma segura mantendo o domínio original
     * Usa redirecionamento relativo ao invés de absoluto
     */
    public static void redirecionarSeguro(HttpServletResponse response, String path) 
            throws IOException {
        
        // Garante que o path comece com / para ser relativo
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        logger.log(Level.INFO, "Redirecionamento seguro para: {0}", path);
        response.sendRedirect(path);
    }
    
    /**
     * Códigos de erro padronizados
     */
    public static final class Codigos {
        public static final String ERRO_404 = "404";
        public static final String ERRO_500 = "500";
        public static final String ERRO_403 = "403";
        public static final String ERRO_400 = "400";
        public static final String TIMEOUT = "TIMEOUT";
        public static final String BANCO_DADOS = "DB_ERROR";
        public static final String VALIDACAO = "VALIDATION";
        public static final String AUTENTICACAO = "AUTH";
        public static final String GERAL = "GERAL";
    }
}