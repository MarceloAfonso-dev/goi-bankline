document.addEventListener("DOMContentLoaded", () => {
  /* ======================================================
     1) SALDO ATUAL
     ====================================================== */
  const saldoSpan = document.getElementById("saldoAtualExtrato");
  fetch("/saldo")
    .then(r => r.json())
    .then(d => {
      if (!saldoSpan) return;
      saldoSpan.textContent = d.error
        ? d.error
        : parseFloat(d.saldo).toLocaleString("pt-BR", {
            style: "currency", currency: "BRL"
          });
    })
    .catch(() => saldoSpan && (saldoSpan.textContent = "--"));

  /* ======================================================
     2) HELPERS
     ====================================================== */
  const fmtBRL = v =>
    Math.abs(v).toLocaleString("pt-BR", { style:"currency", currency:"BRL" });

  const fmtDia = iso => {
    const [y,m,d] = iso.split("-");
    return new Date(y, m-1, d).toLocaleDateString(
             "pt-BR", { day:"numeric", month:"long" });
  };

  /* ======================================================
     3) RENDERIZA LISTA
     ====================================================== */
  const wrapper = document.getElementById("movimentacoesWrapper");
  if (!wrapper) return;               // segurança

  function renderMovimentacoes(movs) {
    wrapper.innerHTML = "";           // limpa antes de renderizar

    /* --- agrupa por data (yyyy-mm-dd) --- */
    const grupos = {};
    movs.forEach(m => (grupos[m.data] ||= []).push(m));

    Object.keys(grupos)
      .sort((a,b) => new Date(b) - new Date(a))
      .forEach(diaISO => {
        /* cabeçalho do dia */
        const header = document.createElement("div");
        header.className = "date-group";
        header.textContent = fmtDia(diaISO);
        wrapper.appendChild(header);

        /* itens do dia */
        grupos[diaISO].forEach(item => {
          const linha = document.createElement("div");
          linha.className = "mov-item";

          /* coluna esquerda (ícone + descrições) */
          const left = document.createElement("div");
          left.className = "item-left";

          const ic = document.createElement("img");
          ic.src = `/static/img/${item.icone || "bag.png"}`;
          ic.alt = "";
          left.appendChild(ic);

          const descBox = document.createElement("div");
          descBox.className = "desc-wrapper";

          const titulo = document.createElement("span");
          titulo.className = "titulo";
          titulo.textContent = item.descricao;
          descBox.appendChild(titulo);

          if (item.sub) {
            const sub = document.createElement("span");
            sub.className = "sub";
            sub.textContent = item.sub;
            descBox.appendChild(sub);
          }
          left.appendChild(descBox);

          /* coluna valor */
          const val = document.createElement("span");
          val.className = "valor " + (item.valor >= 0 ? "positivo" : "negativo");
          val.textContent = (item.valor >= 0 ? "" : "- ") + fmtBRL(item.valor);

          /* junta tudo */
          linha.appendChild(left);
          linha.appendChild(val);
          wrapper.appendChild(linha);
        });
      });
  }

  /* ======================================================
     4) BUSCA DADOS E CHAMA RENDER
     ====================================================== */
  fetch("/extratoDados")
    .then(r => r.json())
    .then(renderMovimentacoes)
    .catch(err => {
      console.error("Erro /extratoDados:", err);
      wrapper.innerHTML = "<p style='padding:20px;'>Erro ao carregar extrato.</p>";
    });
});

document.addEventListener("DOMContentLoaded", () => {
  // botão de “Sair”
  const logoutBtn = document.querySelector(".sair");
  logoutBtn.style.cursor = "pointer";
  logoutBtn.addEventListener("click", () => {
    window.location.href = `${location.protocol}//${location.hostname}:8080/`;
  });

  // clicável no logo (img) e no título (h1) dentro do header
  const logoImg  = document.querySelector("header .icone-goi");
  const logoText = document.querySelector("header h1");

  [logoImg, logoText].forEach(el => {
    if (!el) return;
    el.style.cursor = "pointer";
    el.addEventListener("click", () => {
      window.location.href = `${location.protocol}//${location.hostname}:8080/templates/home.html`;
    });
  });
});
