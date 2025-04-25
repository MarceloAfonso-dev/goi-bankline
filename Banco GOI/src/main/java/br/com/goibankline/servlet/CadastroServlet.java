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

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

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
        String acao = request.getParameter("acao");

        if ("getPares".equals(acao)) {
            handleGetPares(request, response);
        } else if ("verErro".equals(acao)) {
            handleVerErro(request, response);
        } else {
            handleDefaultGet(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String indicesJson = request.getParameter("indicesClicados");

        if (indicesJson == null || indicesJson.isEmpty()) {
            setErrorAndForward(request, response, "Indices não recebidos");
            return;
        }

        Gson gson = new Gson();
        int[] arrayIndices = gson.fromJson(indicesJson, int[].class);

        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        @SuppressWarnings("unchecked")
        List<Par> paresAleatorios = (List<Par>) session.getAttribute("paresAleatorios");

        if (cliente == null || paresAleatorios == null) {
            setErrorAndForward(request, response, "Recarregue a página!");
            return;
        }

        if (!isSenhaValida(cliente.getSenha(), arrayIndices, paresAleatorios)) {
            setErrorAndForward(request, response, "Senha incorreta!");
        } else {
            response.sendRedirect(request.getContextPath() + "/templates/home.html");
        }
    }

    private void handleGetPares(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<Par> pares = (List<Par>) session.getAttribute("paresAleatorios");

        if (pares == null) {
            pares = gerarParesAleatorios();
            session.setAttribute("paresAleatorios", pares);
        }

        Gson gson = new Gson();
        String json = gson.toJson(pares);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    private void handleVerErro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String msgErro = (String) session.getAttribute("loginError");

        if (msgErro != null) {
            session.removeAttribute("loginError");
        }

        response.setContentType("application/json; charset=UTF-8");
        String json = "{\"erro\":" + (msgErro == null ? "null" : "\"" + msgErro + "\"") + "}";
        response.getWriter().write(json);
    }

    private void handleDefaultGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        List<Par> pares = gerarParesAleatorios();
        session.setAttribute("paresAleatorios", pares);

        request.getRequestDispatcher("/templates/login.html").forward(request, response);
    }

    private void setErrorAndForward(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.getSession().setAttribute("loginError", errorMessage);
        request.getRequestDispatcher("/templates/login.html").forward(request, response);
    }

    private boolean isSenhaValida(String senhaDoBanco, int[] arrayIndices, List<Par> paresAleatorios) {
        if (arrayIndices.length != senhaDoBanco.length()) {
            return false;
        }

        for (int i = 0; i < senhaDoBanco.length(); i++) {
            char digitoCorreto = senhaDoBanco.charAt(i);
            int indiceBotao = arrayIndices[i];

            if (indiceBotao < 0 || indiceBotao >= paresAleatorios.size()) {
                return false;
            }

            Par p = paresAleatorios.get(indiceBotao);
            String d = String.valueOf(digitoCorreto);

            if (!d.equals(p.getNum1()) && !d.equals(p.getNum2())) {
                return false;
            }
        }

        return true;
    }

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