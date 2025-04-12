package br.com.goibankline.servlet;

import br.com.goibankline.model.Cliente;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // Representa um “par” de dígitos
    public static class Par {
        private String num1;
        private String num2;
        public Par(String n1, String n2) {
            this.num1 = n1;
            this.num2 = n2;
        }
        public String getNum1() { return num1; }
        public String getNum2() { return num2; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se for AJAX pedindo "acao=getPares", retorna o JSON
        String acao = request.getParameter("acao");
        if ("getPares".equals(acao)) {
            HttpSession session = request.getSession();
            @SuppressWarnings("unchecked")
            List<Par> pares = (List<Par>) session.getAttribute("paresAleatorios");
            if (pares == null) {
                // Se não existir, gera
                pares = gerarParesAleatorios();
                session.setAttribute("paresAleatorios", pares);
            }
            // Responde com JSON
            Gson gson = new Gson();
            String json = gson.toJson(pares);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(json);
            return;
        }
        else if ("verErro".equals(acao)) {
            // Retorna eventual erro em JSON
            HttpSession session = request.getSession();
            String msgErro = (String) session.getAttribute("loginError");
            if (msgErro != null) {
                // Remove da sessão para não exibir de novo depois
                session.removeAttribute("loginError");
            }
            // Ex.: {"erro":"Senha incorreta!"} ou {"erro":null}
            response.setContentType("application/json; charset=UTF-8");
            String json = "{\"erro\":" + (msgErro == null ? "null" : "\"" + msgErro + "\"") + "}";
            response.getWriter().write(json);
            return;
        }

        // CASO NORMAL: exibir a página login.html
        // 1) Gera 5 pares aleatórios e guarda na sessão
        List<Par> pares = gerarParesAleatorios();
        HttpSession session = request.getSession();
        session.setAttribute("paresAleatorios", pares);

        // 2) Verifica se tem cliente na sessão
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        // 3) Encaminha (forward) para o login.html (NÃO altera a URL)
        request.getRequestDispatcher("/templates/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lê array de índices
        String indicesJson = request.getParameter("indicesClicados");
        if (indicesJson == null || indicesJson.isEmpty()) {
            // Armazena msg de erro e forward de volta pra login.html
            request.getSession().setAttribute("loginError", "Indices não recebidos");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
            return;
        }

        // Converte JSON -> int[]
        Gson gson = new Gson();
        int[] arrayIndices = gson.fromJson(indicesJson, int[].class);

        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        @SuppressWarnings("unchecked")
        List<Par> paresAleatorios = (List<Par>) session.getAttribute("paresAleatorios");

        if (cliente == null || paresAleatorios == null) {
            // Manda voltar ao index
            request.getSession().setAttribute("loginError", "Recarregue a página!");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
            return;
        }

        String senhaDoBanco = cliente.getSenha();
        if (arrayIndices.length != senhaDoBanco.length()) {
            request.getSession().setAttribute("loginError", "Senha incompleta!");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
            return;
        }

        // Valida
        boolean senhaValida = true;
        for (int i = 0; i < senhaDoBanco.length(); i++) {
            char digitoCorreto = senhaDoBanco.charAt(i);
            int indiceBotao = arrayIndices[i];

            if (indiceBotao < 0 || indiceBotao >= paresAleatorios.size()) {
                senhaValida = false;
                break;
            }
            Par p = paresAleatorios.get(indiceBotao);
            String d = String.valueOf(digitoCorreto);
            if (!d.equals(p.getNum1()) && !d.equals(p.getNum2())) {
                senhaValida = false;
                break;
            }
        }

        if (!senhaValida) {
            // Armazena msg erro na sessão e forward
            request.getSession().setAttribute("loginError", "Senha incorreta!");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
        } else {
            // Senha correta, manda para home
            response.sendRedirect(request.getContextPath() + "/templates/home.html");
        }
    }

    // Gera 5 pares aleatórios (0..9 sem repetição)
    private List<Par> gerarParesAleatorios() {
        List<Integer> lista = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lista.add(i);
        }
        Collections.shuffle(lista);

        List<Par> pares = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int n1 = lista.get(i * 2);
            int n2 = lista.get(i * 2 + 1);
            pares.add(new Par(String.valueOf(n1), String.valueOf(n2)));
        }
        return pares;
    }
}
