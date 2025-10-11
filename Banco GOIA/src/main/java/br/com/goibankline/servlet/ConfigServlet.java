package br.com.goibankline.servlet;

import br.com.goibankline.util.EnvironmentConfig;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/config")
public class ConfigServlet extends HttpServlet {

    private final EnvironmentConfig envConfig = EnvironmentConfig.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        Map<String, Object> config = new HashMap<>();
        config.put("baseUrl", envConfig.getBaseUrl());
        config.put("apiBaseUrl", envConfig.getApiBaseUrl());
        config.put("contextPath", envConfig.getContextPath());
        config.put("environment", envConfig.getEnvironment());
        config.put("isAws", envConfig.isAwsEnvironment());
        config.put("isLocal", envConfig.isLocalEnvironment());

        String jsonResponse = gson.toJson(config);
        response.getWriter().write(jsonResponse);
    }
}
