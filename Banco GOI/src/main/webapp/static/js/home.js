// /static/js/home.js
document.addEventListener("DOMContentLoaded", () => {
  // 1) Referências no DOM
  const toggleVis        = document.getElementById("toggle-visibility");
  const saldoEl          = document.querySelector(".container-saldo .saldo");
  const limiteEl         = document.querySelector(".container-saldo .limite-valor");
  const limiteCartaoEl   = document.querySelector(".limite-cartao");
  const valorFaturaEl    = document.querySelector(".valor-fatura");
  const toggleSaldoBtn   = document.getElementById("toggle-saldo");
  const containerSaldo   = document.querySelector(".container-saldo");
  const toggleCartoesBtn = document.getElementById("toggle-cartoes");
  const containerCartoes = document.querySelector(".container-cartoes");

  // 2) Variáveis que vão guardar os valores reais formatados
  let originalSaldo      = "";
  let originalLimite     = "";
  let originalLimiteCart = "";
  let originalValorFat   = "";

  // 3) Função para formatar número em R$ pt-BR
  const fmtBRL = num =>
    new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL"
    }).format(num);

  // 4) Buscar saldo / limites do servidor
  fetch("/saldo")
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        saldoEl.textContent  = data.error;
        limiteEl.textContent = "";
        return;
      }
      // transforma strings em Number
      const sal  = parseFloat(data.saldo);
      const lim  = parseFloat(data.limiteDisponivel);
      originalSaldo      = fmtBRL(sal);
      originalLimite     = fmtBRL(lim);

      // atualiza DOM
      saldoEl.textContent  = originalSaldo;
      limiteEl.textContent = originalLimite;

      // cartão (opcional)
      if (data.limiteCartao) {
        const limCart = parseFloat(data.limiteCartao);
        originalLimiteCart = fmtBRL(limCart);
        limiteCartaoEl.textContent = originalLimiteCart;
      }
      if (data.valorFatura) {
        const valFat = parseFloat(data.valorFatura);
        originalValorFat = fmtBRL(valFat);
        valorFaturaEl.textContent = originalValorFat;
      }
    })
    .catch(err => {
      console.error("Erro ao carregar /saldo:", err);
      saldoEl.textContent = "Erro ao carregar";
    });

  // 5) Toggle de visibilidade
  const visOn  = "/static/img/visibility.png";
  const visOff = "/static/img/visibility-off.png";
  toggleVis.addEventListener("click", () => {
    const mostrando = saldoEl.textContent === originalSaldo;
    if (mostrando) {
      // oculta
      saldoEl.textContent      = "****";
      limiteEl.textContent     = "****";
      if (limiteCartaoEl)   limiteCartaoEl.textContent   = "****";
      if (valorFaturaEl)    valorFaturaEl.textContent    = "****";
      toggleVis.src = visOff;
      saldoEl.classList.add("hidden-value");
      limiteEl.classList.add("hidden-value");
    } else {
      // mostra
      saldoEl.textContent      = originalSaldo;
      limiteEl.textContent     = originalLimite;
      if (limiteCartaoEl)   limiteCartaoEl.textContent   = originalLimiteCart;
      if (valorFaturaEl)    valorFaturaEl.textContent    = originalValorFat;
      toggleVis.src = visOn;
      saldoEl.classList.remove("hidden-value");
      limiteEl.classList.remove("hidden-value");
    }
  });

  // 6) Toggle expandir/recolher SALDO
  toggleSaldoBtn.addEventListener("click", () => {
    containerSaldo.classList.toggle("retracted");
    toggleSaldoBtn.classList.toggle("rotated");
  });

  // 7) Toggle expandir/recolher CARTÕES
  toggleCartoesBtn.addEventListener("click", () => {
    containerCartoes.classList.toggle("retracted");
    toggleCartoesBtn.classList.toggle("rotated");
  });
});

// /static/js/home.js
document.addEventListener("DOMContentLoaded", () => {
  const logoutBtn = document.querySelector(".sair");
  logoutBtn.style.cursor = "pointer";
  logoutBtn.addEventListener("click", () => {
    // força o redirecionamento para a porta 8080
    window.location.href = `${location.protocol}//${location.hostname}:8080/`;
  });
});
