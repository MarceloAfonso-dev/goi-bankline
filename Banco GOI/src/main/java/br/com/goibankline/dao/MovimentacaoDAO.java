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
                "SELECT * FROM (                                                         " +
                        "  /* ---------- Pagamentos (sempre débito) ---------- */                 " +
                        "  SELECT                                                                 " +
                        "     p.Data_Pagamento            AS data,                                " +
                        "     p.Descricao                 AS descricao,                           " +
                        "     p.Valor                     AS valor,                               " + // negativo já salvo
                        "     'Débito'                    AS tipo,                                " +
                        "     'Pagamento'                 AS sub,                                 " +
                        "     'boleto.png'                AS icone                                " +
                        "  FROM Pagamento p                                                        " +
                        "  WHERE p.Id_Conta = ?                                                    " +
                        "                                                                           " +
                        "  UNION ALL                                                               " +
                        "                                                                           " +
                        "  /* ---------- Transferências ---------- */                              " +
                        "  SELECT                                                                  " +
                        "     t.Data_Transferencia         AS data,                                " +
                        "     CONCAT('Pix ', t.Tipo_Transferencia) AS descricao,                   " +
                        "     t.Valor                      AS valor,                               " + // já + ou -
                        "     CASE WHEN t.Valor < 0 THEN 'Débito' ELSE 'Crédito' END AS tipo,      " +
                        "     'Transferência'              AS sub,                                 " +
                        "     CASE WHEN t.Valor < 0 THEN 'bag.png' ELSE 'pix-in.png' END AS icone  " +
                        "  FROM Transferencia t                                                    " +
                        "  WHERE (t.Id_Conta_Origem  = ? AND t.Valor < 0)                          " +
                        "     OR (t.Id_Conta_Destino = ? AND t.Valor > 0)                          " +
                        ") AS m                                                                    " +
                        "ORDER BY m.data DESC, m.sub;                                              ";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            /* parâmetros: 1-Pagto | 2-Transf débito | 3-Transf crédito */
            st.setInt(1, idConta);
            st.setInt(2, idConta);
            st.setInt(3, idConta);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Movimentacao m = new Movimentacao();
                    m.setData      (rs.getDate("data").toLocalDate());
                    m.setDescricao (rs.getString("descricao"));
                    m.setValor     (rs.getBigDecimal("valor"));
                    m.setTipo      (rs.getString("tipo"));
                    m.setSub       (rs.getString("sub"));
                    m.setIcone     (rs.getString("icone"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return lista;
    }
}
