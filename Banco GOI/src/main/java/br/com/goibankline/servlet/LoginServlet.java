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

        // Verifica se é uma chamada AJAX pedindo os pares em JSON
        String acao = request.getParameter("acao");
        if ("getPares".equals(acao)) {
            // Retorna os pares que já foram gerados e estão na sessão
            HttpSession session = request.getSession();
            @SuppressWarnings("unchecked")
            List<Par> pares = (List<Par>) session.getAttribute("paresAleatorios");
            if (pares == null) {
                // Se não existir, geramos agora (pode acontecer se recarregar AJAX sem ter ido no GET normal)
                pares = gerarParesAleatorios();
                session.setAttribute("paresAleatorios", pares);
            }

            // Converte em JSON e envia de volta
            Gson gson = new Gson();
            String json = gson.toJson(pares);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(json);
            return;
        }

        // Se não for AJAX, então é acesso “normal” para exibir o login.html
        // 1) Gera 5 pares aleatórios
        List<Par> pares = gerarParesAleatorios();

        // 2) Armazena na sessão para o doPost (ou para o AJAX)
        HttpSession session = request.getSession();
        session.setAttribute("paresAleatorios", pares);

        // 3) Verifica se já existe um cliente logado
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            // Se não tiver cliente na sessão, manda voltar para index.html
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        // 4) Encaminha para o login.html (estático)
        //    (O JS dentro dele vai chamar ?acao=getPares e popular os botões)
        request.getRequestDispatcher("/templates/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Lê o JSON do array de índices clicados (ex.: "[0,3,1,2,4,0]")
        String indicesJson = request.getParameter("indicesClicados");
        if (indicesJson == null || indicesJson.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/templates/login.html?erro=Indices%20não%20recebidos");
            return;
        }

        // 2) Converte para um array de int usando Gson
        Gson gson = new Gson();
        int[] arrayIndices = gson.fromJson(indicesJson, int[].class);

        // 3) Recupera o cliente e os pares da sessão
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        @SuppressWarnings("unchecked")
        List<Par> paresAleatorios = (List<Par>) session.getAttribute("paresAleatorios");

        // Se não existir, redireciona para forçar o GET
        if (cliente == null || paresAleatorios == null) {
            response.sendRedirect(request.getContextPath() + "/index.html?erro=Recarregue%20a%20página");
            return;
        }

        String senhaDoBanco = cliente.getSenha(); // ex.: "124578"

        // 4) Se a senha tem 6 dígitos, esperamos ter 6 cliques
        if (arrayIndices.length != senhaDoBanco.length()) {
            response.sendRedirect(request.getContextPath() + "/templates/login.html?erro=Senha%20incompleta");
            return;
        }

        // 5) Valida se cada dígito da senha está no Par correspondente
        boolean senhaValida = true;
        for (int i = 0; i < senhaDoBanco.length(); i++) {
            char digitoCorreto = senhaDoBanco.charAt(i);
            int indiceBotao = arrayIndices[i]; // qual botão foi clicado nessa posição

            // Se o usuário clicou no índice do backspace ou fora do array de pares
            if (indiceBotao < 0 || indiceBotao >= paresAleatorios.size()) {
                senhaValida = false;
                break;
            }

            Par p = paresAleatorios.get(indiceBotao);
            // Se o dígito (ex. '1') não está em p.num1 ou p.num2, falha
            String d = String.valueOf(digitoCorreto);
            if (!d.equals(p.getNum1()) && !d.equals(p.getNum2())) {
                senhaValida = false;
                break;
            }
        }

        // 6) Redireciona conforme resultado
        if (!senhaValida) {
            response.sendRedirect(request.getContextPath() + "/templates/login.html?erro=Senha%20incorreta");
        } else {
            // Logado
            response.sendRedirect(request.getContextPath() + "/templates/home.html");
        }
    }

    // Função auxiliar: gerar 5 pares de dígitos aleatórios (sem repetição).
    private List<Par> gerarParesAleatorios() {
        // Exemplo: embaralha [0..9] e agrupa 2 a 2
        List<Integer> lista = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lista.add(i);
        }
        Collections.shuffle(lista);

        // Agora criamos 5 pares
        List<Par> pares = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int num1 = lista.get(i * 2);
            int num2 = lista.get(i * 2 + 1);
            pares.add(new Par(String.valueOf(num1), String.valueOf(num2)));
        }
        return pares;
    }
}
