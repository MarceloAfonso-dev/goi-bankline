// /static/js/home.js
document.addEventListener("DOMContentLoaded", () => {

  /* =========================================================
     BUSCA AGÊNCIA / CONTA — MESMA LÓGICA DO /saldo
     ========================================================= */
  fetch("/conta")                       // igual ao que funciona em /saldo
    .then(r => {
      if (!r.ok) throw new Error(r.status);
      return r.json();
    })
    .then(data => {
      console.log("[/conta]:", data);   // deve ver {agencia:"5481", conta:"548123"}

      const ag = document.getElementById("agencia");
      const cc = document.getElementById("conta");

      if (!ag || !cc) {
        console.warn("#agencia ou #conta não encontrados");
        return;
      }
      ag.textContent = `ag: ${data.agencia ?? "----"}`;
      cc.textContent = `c/c: ${data.conta   ?? "------"}`;
    })
    .catch(err => console.error("Falha /conta", err));

  // === 1) Referências no DOM ===
  const toggleVis        = document.getElementById("toggle-visibility");
  const saldoEl          = document.querySelector(".container-saldo .saldo");
  const limiteEl         = document.querySelector(".container-saldo .limite-valor");
  const limiteCartaoEl   = document.querySelector(".limite-cartao");
  const valorFaturaEl    = document.querySelector(".valor-fatura");
  const toggleSaldoBtn   = document.getElementById("toggle-saldo");
  const containerSaldo   = document.querySelector(".container-saldo");
  const toggleCartoesBtn = document.getElementById("toggle-cartoes");
  const containerCartoes = document.querySelector(".container-cartoes");
  const logoutBtn        = document.querySelector(".sair");
  const sidebarItems     = document.querySelectorAll(".sidebar__list li");

  // === 2) Guardar valores originais ===
  let originalSaldo      = "";
  let originalLimite     = "";
  let originalLimiteCart = "";
  let originalValorFat   = "";

  // === 3) Função de formatação pt-BR ===
  const fmtBRL = num =>
    new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(num);

  // === 4) Busca dados de /saldo ===
  fetch("/saldo")
    .then(r => r.json())
    .then(data => {
      if (data.error) {
        if (saldoEl) saldoEl.textContent = data.error;
        if (limiteEl) limiteEl.textContent = "";
        return;
      }
      const sal = parseFloat(data.saldo);
      const lim = parseFloat(data.limiteDisponivel);
      originalSaldo  = fmtBRL(sal);
      originalLimite = fmtBRL(lim);

      if (saldoEl)  saldoEl.textContent  = originalSaldo;
      if (limiteEl) limiteEl.textContent = originalLimite;

      if (data.limiteCartao && limiteCartaoEl) {
        const limCart = parseFloat(data.limiteCartao);
        originalLimiteCart = fmtBRL(limCart);
        limiteCartaoEl.textContent = originalLimiteCart;
      }
      if (data.valorFatura && valorFaturaEl) {
        const valFat = parseFloat(data.valorFatura);
        originalValorFat = fmtBRL(valFat);
        valorFaturaEl.textContent = originalValorFat;
      }
    })
    .catch(err => {
      console.error("Erro ao carregar /saldo:", err);
      if (saldoEl) saldoEl.textContent = "Erro ao carregar";
    });

  // === 5) Toggle visibilidade ===
  if (toggleVis) {
    const visOn  = "/static/img/visibility.png";
    const visOff = "/static/img/visibility-off.png";
    toggleVis.style.cursor = "pointer";

    toggleVis.addEventListener("click", () => {
      const oculto = saldoEl.textContent !== originalSaldo;
      if (oculto) {
        // mostrar
        saldoEl.textContent      = originalSaldo;
        limiteEl.textContent     = originalLimite;
        if (limiteCartaoEl)   limiteCartaoEl.textContent   = originalLimiteCart;
        if (valorFaturaEl)    valorFaturaEl.textContent    = originalValorFat;
        toggleVis.src = visOn;
        saldoEl.classList.remove("hidden-value");
        limiteEl.classList.remove("hidden-value");
      } else {
        // ocultar
        saldoEl.textContent      = "****";
        limiteEl.textContent     = "****";
        if (limiteCartaoEl)   limiteCartaoEl.textContent   = "****";
        if (valorFaturaEl)    valorFaturaEl.textContent    = "****";
        toggleVis.src = visOff;
        saldoEl.classList.add("hidden-value");
        limiteEl.classList.add("hidden-value");
      }
    });
  }

  // === 6) Toggle expandir/recolher SALDO ===
  if (toggleSaldoBtn && containerSaldo) {
    toggleSaldoBtn.addEventListener("click", () => {
      containerSaldo.classList.toggle("retracted");
      toggleSaldoBtn.classList.toggle("rotated");
    });
  }

  // === 7) Toggle expandir/recolher CARTÕES ===
  if (toggleCartoesBtn && containerCartoes) {
    toggleCartoesBtn.addEventListener("click", () => {
      containerCartoes.classList.toggle("retracted");
      toggleCartoesBtn.classList.toggle("rotated");
    });
  }

  // === 8) Logout ===
  if (logoutBtn) {
    logoutBtn.style.cursor = "pointer";
    logoutBtn.addEventListener("click", () => {
      window.location.href = `${location.protocol}//${location.hostname}:8080/`;
    });
  }

  // === 9) Tornar <li> da sidebar clicável ===
  sidebarItems.forEach(li => {
    li.style.cursor = "pointer";
    li.addEventListener("click", () => {
      const link = li.querySelector("a");
      if (link) {
        const href = link.getAttribute("href");
        if (href && href !== "#") {
          window.location.href = `${location.protocol}//${location.host}${href}`;
        }
      }
    });
  });
});
