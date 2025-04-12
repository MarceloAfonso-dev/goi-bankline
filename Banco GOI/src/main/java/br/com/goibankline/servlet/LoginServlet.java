package br.com.goibankline.servlet;

import br.com.goibankline.model.Cliente;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Verifica se já existe um cliente na sessão
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            // Se não tiver cliente na sessão, manda voltar para index.html
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }
        // Encaminha para o login.html (que tem o teclado virtual)
        request.getRequestDispatcher("/templates/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Recupera o JSON do array de índices clicados
        //    Ex.: "[0,2,4,1,0,3]"
        String indicesJson = request.getParameter("indicesClicados");
        if (indicesJson == null || indicesJson.isEmpty()) {
            // Ajuste o caminho para o seu login.html real
            response.sendRedirect(request.getContextPath() + "/templates/login.html?erro=Indices%20não%20recebidos");
            return;
        }

        // 2) Converte para um array de int usando Gson
        Gson gson = new Gson();
        int[] arrayIndices = gson.fromJson(indicesJson, int[].class);

        // 3) Recupera o cliente da sessão e a senha do banco
        HttpSession session = request.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/index.html?erro=Cliente%20não%20encontrado");
            return;
        }
        String senhaDoBanco = cliente.getSenha(); // Ex.: "124578" (6 dígitos)

        // 4) Se a senha tem 6 dígitos, esperamos ter 6 cliques
        if (arrayIndices.length != senhaDoBanco.length()) {
            response.sendRedirect(request.getContextPath() + "/templates/login.html?erro=Senha%20incompleta");
            return;
        }

        // 5) Mapeamento fixo dos 5 botões de duplas + 1 de backspace
        //    Indice 0 -> "1","2"
        //    Indice 1 -> "3","4"
        //    Indice 2 -> "5","6"
        //    Indice 3 -> "7","8"
        //    Indice 4 -> "9","10"
        //    Indice 5 -> BACKSPACE (não deve vir na lista final)
        String[][] pares = {
                {"1","2"},   // botão 0
                {"3","4"},   // botão 1
                {"5","6"},   // botão 2
                {"7","8"},   // botão 3
                {"9","10"}   // botão 4
                // botão 5 é backspace
        };

        // 6) Valida se cada dígito da senha está no par do índice correspondente
        boolean senhaValida = true;
        for (int i = 0; i < senhaDoBanco.length(); i++) {
            char digitoCorreto = senhaDoBanco.charAt(i);  // ex.: '1'
            int indiceBotao = arrayIndices[i];            // ex.: 0

            // Se o índice for fora dos 0..4, é inválido
            if (indiceBotao < 0 || indiceBotao > 4) {
                senhaValida = false;
                break;
            }

            // Verifica se digitoCorreto está entre os pares
            String[] par = pares[indiceBotao];   // ex.: {"1","2"}
            String digitoStr = String.valueOf(digitoCorreto);

            if (!digitoStr.equals(par[0]) && !digitoStr.equals(par[1])) {
                senhaValida = false;
                break;
            }
        }

        // 7) Redireciona conforme resultado
        if (!senhaValida) {
            response.sendRedirect(request.getContextPath() + "/templates/login.html?erro=Senha%20incorreta");
        } else {
            // Senha correta => vai para home
            response.sendRedirect(request.getContextPath() + "/templates/home.html");
        }
    }
}
