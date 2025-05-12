document.addEventListener("DOMContentLoaded", () => {
    const tbody = document.getElementById("extratoBody");
  
    const formatarReais = (valor) => {
      return parseFloat(valor).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
      });
    };
  
    const formatarData = (dataStr) => {
      const [ano, mes, dia] = dataStr.split("-");
      return `${dia}/${mes}/${ano}`;
    };
  
    // JSON fictício extenso
    const movimentacoes = [
      { data: "2025-05-12", descricao: "PIX recebido - Cliente A", tipo: "Crédito", valor: 1250 },
      { data: "2025-05-12", descricao: "PIX enviado - Oficina", tipo: "Débito", valor: -299.90 },
      { data: "2025-05-11", descricao: "Spotify", tipo: "Débito", valor: -19.90 },
      { data: "2025-05-10", descricao: "Rendimento CDB", tipo: "Crédito", valor: 12.34 },
      { data: "2025-05-09", descricao: "Supermercado", tipo: "Débito", valor: -237.49 },
      { data: "2025-05-08", descricao: "PIX recebido - Amigo B", tipo: "Crédito", valor: 500 },
      { data: "2025-05-07", descricao: "Saque 24h", tipo: "Débito", valor: -300 },
      { data: "2025-05-06", descricao: "Pagamento Boleto", tipo: "Débito", valor: -789.00 },
      { data: "2025-05-05", descricao: "PIX recebido - Cliente C", tipo: "Crédito", valor: 1000 },
      { data: "2025-05-04", descricao: "Restaurante", tipo: "Débito", valor: -129.90 },
      { data: "2025-05-03", descricao: "Netflix", tipo: "Débito", valor: -39.90 },
      { data: "2025-05-02", descricao: "Transferência GOI Rewards", tipo: "Crédito", valor: 35 },
      { data: "2025-05-01", descricao: "Salário", tipo: "Crédito", valor: 3850 },
      { data: "2025-04-30", descricao: "Compra online - Magazine", tipo: "Débito", valor: -450 },
      { data: "2025-04-29", descricao: "Depósito via boleto", tipo: "Crédito", valor: 600 },
      { data: "2025-04-28", descricao: "Pagamento de energia", tipo: "Débito", valor: -220.70 },
      { data: "2025-04-27", descricao: "Pagamento de água", tipo: "Débito", valor: -80.22 },
      { data: "2025-04-26", descricao: "PIX recebido - Irmão", tipo: "Crédito", valor: 300 },
      { data: "2025-04-25", descricao: "Transferência entre contas", tipo: "Débito", valor: -750 },
      { data: "2025-04-24", descricao: "Cheque especial contratado", tipo: "Crédito", valor: 1000 }
      // adicione mais se quiser testar scroll
    ];
  
    movimentacoes.forEach(item => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${formatarData(item.data)}</td>
        <td>${item.descricao}</td>
        <td>${item.tipo}</td>
        <td class="${item.valor >= 0 ? "positivo" : "negativo"}">
          ${item.valor >= 0 ? "" : "- "}${formatarReais(Math.abs(item.valor))}
        </td>
      `;
      tbody.appendChild(tr);
    });
  });
  