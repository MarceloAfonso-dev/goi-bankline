package br.com.goibankline.filter;

import br.com.goibankline.util.EnvironmentConfig;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class DynamicConfigFilter implements Filter {

    private EnvironmentConfig envConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        envConfig = EnvironmentConfig.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Adiciona headers com informações de configuração
        httpResponse.setHeader("X-App-Base-URL", envConfig.getBaseUrl());
        httpResponse.setHeader("X-App-API-URL", envConfig.getApiBaseUrl());
        httpResponse.setHeader("X-App-Environment", envConfig.getEnvironment());

        // Adiciona atributos à requisição para uso nos servlets
        httpRequest.setAttribute("baseUrl", envConfig.getBaseUrl());
        httpRequest.setAttribute("apiBaseUrl", envConfig.getApiBaseUrl());
        httpRequest.setAttribute("environment", envConfig.getEnvironment());

        // Configura CORS para ambiente AWS
        if (envConfig.isAwsEnvironment()) {
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
