document.addEventListener("DOMContentLoaded", () => {
    const tbody = document.getElementById("extratoBody");
  
    // Helper para formatar R$
    const formatarReais = (valor) => {
      return parseFloat(valor).toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL",
      });
    };
  
    // Helper para formatar data (yyyy-mm-dd → dd/mm/yyyy)
    const formatarData = (dataStr) => {
      const [ano, mes, dia] = dataStr.split("-");
      return `${dia}/${mes}/${ano}`;
    };
  
    // JSON simulado de movimentações financeiras
    const lancamentos = [
      { data: "2025-05-11", descricao: "Pix recebido - Maria", tipo: "Crédito", valor: 200 },
      { data: "2025-05-10", descricao: "Transferência TED - João", tipo: "Débito", valor: -350 },
      { data: "2025-05-09", descricao: "Pagamento boleto", tipo: "Débito", valor: -120.50 },
      { data: "2025-05-08", descricao: "Salário", tipo: "Crédito", valor: 3450 },
      { data: "2025-05-07", descricao: "Transferência GOI Rewards", tipo: "Crédito", valor: 50 },
      { data: "2025-05-06", descricao: "Assinatura Spotify", tipo: "Débito", valor: -19.90 },
      { data: "2025-05-06", descricao: "Assinatura Netflix", tipo: "Débito", valor: -39.90 },
      { data: "2025-05-05", descricao: "Pix enviado - Farmácia", tipo: "Débito", valor: -89.75 },
      { data: "2025-05-04", descricao: "PIX - Cliente Ana", tipo: "Crédito", valor: 1_200 },
      { data: "2025-05-04", descricao: "Depósito via boleto", tipo: "Crédito", valor: 500 },
      { data: "2025-05-03", descricao: "Transferência entre contas", tipo: "Débito", valor: -1000 },
      { data: "2025-05-03", descricao: "Pix recebido - Pedro", tipo: "Crédito", valor: 620 },
      { data: "2025-05-02", descricao: "Compra Mercado Livre", tipo: "Débito", valor: -230.99 },
      { data: "2025-05-01", descricao: "Compra Amazon", tipo: "Débito", valor: -139.50 },
      { data: "2025-04-30", descricao: "Rendimento CDB", tipo: "Crédito", valor: 45.88 },
      { data: "2025-04-29", descricao: "Pix recebido - Tio Paulo", tipo: "Crédito", valor: 300 },
      { data: "2025-04-28", descricao: "Recarga celular", tipo: "Débito", valor: -15 },
      { data: "2025-04-27", descricao: "Seguro auto", tipo: "Débito", valor: -189.99 },
      { data: "2025-04-26", descricao: "Pagamento energia", tipo: "Débito", valor: -210.70 },
      { data: "2025-04-25", descricao: "Pagamento água", tipo: "Débito", valor: -84.22 },
      // ... adicione mais se quiser testar scroll longo
    ];
  
    // Preenche a tabela com os lançamentos
    lancamentos.forEach((item) => {
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
  