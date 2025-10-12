package br.com.goibankline.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import br.com.goibankline.model.Cliente;

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

        // Verifica se é chamada AJAX para obter pares
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
            // Retorna em JSON
            Gson gson = new Gson();
            String json = gson.toJson(pares);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(json);
            return;
        }
        else if ("verErro".equals(acao)) {
            // Retorna em JSON a mensagem de erro (se houver)
            HttpSession session = request.getSession();
            String msgErro = (String) session.getAttribute("loginError");
            // Se existir, remove da sessão para não repetir
            if (msgErro != null) {
                session.removeAttribute("loginError");
            }

            // Ex.: {"erro":"Senha incorreta!"} ou {"erro":null}
            response.setContentType("application/json; charset=UTF-8");
            String json = "{\"erro\":" + (msgErro == null ? "null" : "\"" + msgErro + "\"") + "}";
            response.getWriter().write(json);
            return;
        }

        // Caso normal (sem acao): exibir login.html
        HttpSession session = request.getSession();

        // Gera 5 pares aleatórios e guarda na sessão para uso posterior
        List<Par> pares = gerarParesAleatorios();
        session.setAttribute("paresAleatorios", pares);

        // SEGURANÇA: Verifica se cliente temporário está na sessão (processo de login)
        Cliente clienteTemporario = (Cliente) session.getAttribute("clienteTemporario");
        if (clienteTemporario == null) {
            // Se não houver cliente temporário, redirect para index.html
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        // Forward para login.html (não muda URL)
        request.getRequestDispatcher("/templates/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lê os índices clicados
        String indicesJson = request.getParameter("indicesClicados");
        if (indicesJson == null || indicesJson.isEmpty()) {
            // Erro técnico - não deve ser exibido ao usuário
            request.getSession().setAttribute("loginError", "Indices não recebidos");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
            return;
        }

        // Converte JSON -> int[]
        Gson gson = new Gson();
        int[] arrayIndices = gson.fromJson(indicesJson, int[].class);

        HttpSession session = request.getSession();
        // SEGURANÇA: Usa cliente temporário (ainda não autenticado)
        Cliente cliente = (Cliente) session.getAttribute("clienteTemporario");
        @SuppressWarnings("unchecked")
        List<Par> paresAleatorios = (List<Par>) session.getAttribute("paresAleatorios");

        if (cliente == null || paresAleatorios == null) {
            request.getSession().setAttribute("loginError", "Recarregue a página!");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
            return;
        }

        // Verifica tamanho
        String senhaDoBanco = cliente.getSenha(); // ex. "124578"
        if (arrayIndices.length != senhaDoBanco.length()) {
            request.getSession().setAttribute("loginError", "Senha incompleta!");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
            return;
        }

        // Valida cada dígito da senha em relação aos pares clicados
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

        // Se falhou, define loginError e forward p/ login.html
        if (!senhaValida) {
            request.getSession().setAttribute("loginError", "Senha incorreta!");
            request.getRequestDispatcher("/templates/login.html").forward(request, response);
        } else {
            // SEGURANÇA: Senha correta - agora sim autentica o usuário
            session.setAttribute("cliente", cliente); // Cliente autenticado
            session.removeAttribute("clienteTemporario"); // Remove temporário
            session.setAttribute("usuarioLogado", true); // Flag de autenticação
            
            // Forward para home (mantém o domínio atual)
            request.getRequestDispatcher("/templates/home.html").forward(request, response);
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
