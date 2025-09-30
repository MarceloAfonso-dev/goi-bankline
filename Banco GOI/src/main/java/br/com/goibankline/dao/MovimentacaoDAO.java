package br.com.goibankline.dao;

import br.com.goibankline.model.Movimentacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Consulta unificada de Pagamentos + Transferências. */
public class MovimentacaoDAO {

    /** Retorna lista ordenada DESC por data. */
    public List<Movimentacao> listarPorConta(int idConta) {

        List<Movimentacao> lista = new ArrayList<>();

        String sql =
                "SELECT * FROM (                                                            " +
                        "  /* ---------------- Pagamentos ---------------- */                       " +
                        "  SELECT                                                                   " +
                        "     p.Data_Pagamento              AS data,                                " +
                        "     p.Descricao                   AS descricao_raw,                       " +
                        "     p.Valor                       AS valor,                               " +
                        "     'Pagamento'                   AS categoria,                           " +
                        "     NULL                          AS nome_origem,                         " +
                        "     NULL                          AS nome_destino,                        " +
                        "     NULL                          AS tipo_pix                             " +
                        "  FROM Pagamento p                                                         " +
                        "  WHERE p.Id_Conta = ?                                                     " +
                        "                                                                            " +
                        "  UNION ALL                                                                " +
                        "                                                                            " +
                        "  /* ---------------- Transferências ---------------- */                   " +
                        "  SELECT                                                                   " +
                        "     t.DataTransferencia           AS data,                                " +
                        "     NULL                          AS descricao_raw,                       " +
                        "     t.Valor                       AS valor,                               " +
                        "     'Transferencia'               AS categoria,                           " +
                        "     cliOrig.Nome                  AS nome_origem,                         " +
                        "     cliDest.Nome                  AS nome_destino,                        " +
                        "     t.TipoTransferencia           AS tipo_pix                             " +
                        "  FROM Transferencia t                                                      " +
                        "  JOIN Conta   cOrig  ON cOrig.IdConta = t.IdContaOrigem                   " +
                        "  JOIN Cliente cliOrig ON cliOrig.ID   = cOrig.ID_Cliente                  " +
                        "  JOIN Conta   cDest  ON cDest.IdConta = t.IdContaDestino                  " +
                        "  JOIN Cliente cliDest ON cliDest.ID   = cDest.ID_Cliente                  " +
                        "  WHERE (t.IdContaOrigem  = ? AND t.Valor < 0)                             " +
                        "     OR (t.IdContaDestino = ? AND t.Valor > 0)                             " +
                        ") AS m                                                                     " +
                        "ORDER BY m.data DESC, m.categoria;";


        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            /* parâmetros: 1-Pagto | 2-Transf débito | 3-Transf crédito */
            st.setInt(1, idConta);
            st.setInt(2, idConta);
            st.setInt(3, idConta);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Movimentacao m = new Movimentacao();
                    m.setData (rs.getDate("data").toLocalDate());
                    m.setValor(rs.getBigDecimal("valor"));

                    String categoria = rs.getString("categoria");

                    if ("Pagamento".equals(categoria)) {
                        m.setDescricao(rs.getString("descricao_raw"));
                        m.setTipo("Débito");
                        m.setSub("Pagamento");
                        m.setIcone("boleto.png");
                    } else { // Transferência
                        String nomeOrig = rs.getString("nome_origem");
                        String nomeDest = rs.getString("nome_destino");
                        String tipoPix  = rs.getString("tipo_pix");          // PIX / TED / DOC

                        boolean euSouOrigem = m.getValor().signum() < 0;     // negativo = saiu $$

                        String desc;
                        if (euSouOrigem) {
                            // Transferência enviada - remover redundância "Pix PIX"
                            desc = tipoPix + " enviado para " + nomeDest;
                            m.setTipo("Débito");
                            m.setIcone("bag.png");
                        } else {
                            // Transferência recebida - remover redundância "Pix PIX"
                            desc = tipoPix + " recebido de " + nomeOrig;
                            m.setTipo("Crédito");
                            m.setIcone("pix-in.png");
                        }
                        m.setDescricao(desc);
                        m.setSub("Transferência");
                    }
                    lista.add(m);
                }

            }
        } catch (SQLException e) { e.printStackTrace(); }

        return lista;
    }
}
