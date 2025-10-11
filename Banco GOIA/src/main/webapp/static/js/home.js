// /static/js/home.js
document.addEventListener("DOMContentLoaded", () => {

  /* =========================================================
     BUSCA AGÊNCIA / CONTA + nome do cliente
     ========================================================= */
  fetch("/conta")
    .then(r => {
      if (!r.ok) throw new Error(r.status);
      return r.json();
    })
    .then(data => {
      console.log("[/conta]:", data);   // {agencia:"5481", conta:"548123", nome:"Marcelo"}

      // 1) agencia / conta
      const ag = document.getElementById("agencia");
      const cc = document.getElementById("conta");
      if (ag) ag.textContent = `ag: ${data.agencia ?? "----"}`;
      if (cc) cc.textContent = `c/c: ${data.conta   ?? "------"}`;

      // 2) saudação
      const h3 = document.getElementById("boasVindas");
      if (h3 && data.nome) {
        h3.textContent = `Olá ${data.nome}, que bom te ver por aqui!`;
      }
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
      if (window.urlManager) {
        window.urlManager.logout();
      } else {
        window.location.href = '/';
      }
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
          if (window.urlManager) {
            window.location.href = window.urlManager.buildPageUrl(href);
          } else {
            window.location.href = `${location.protocol}//${location.host}${href}`;
          }
        }
      }
    });
  });

  // clicável no logo (img) e no título (h1) dentro do header
  const logoImg  = document.querySelector("header .icone-goia");
  const logoText = document.querySelector("header h1");

  [logoImg, logoText].forEach(el => {
    if (!el) return;
    el.style.cursor = "pointer";
    el.addEventListener("click", () => {
      if (window.urlManager) {
        window.urlManager.goHome();
      } else {
        window.location.href = '/templates/home.html';
      }
    });
  });
});


/* =========================================================
   POP-UPS DA SIDEBAR  —  PAGAMENTOS, INVESTIMENTOS, PRODUTOS
   ========================================================= */
document.addEventListener('DOMContentLoaded', () => {
  const itensSidebar = document.querySelectorAll('.sidebar__list li');

  // mapeia id do popup ⇢ elemento
  const popups = {
    pagamentos:    document.getElementById('popup-pagamentos'),
    investimentos: document.getElementById('popup-investimentos'),
    produtos:      document.getElementById('popup-produtos')
  };

  /* -------- Abrir pop-ups -------- */
  // 0 = pagamentos  |  2 = investimentos  |  3 = produtos
  itensSidebar[0].addEventListener('click', e => {
    e.stopPropagation();                             // impede cliques “duplos”
    popups.pagamentos.classList.remove('hidden');
    document.body.classList.add('popup-active');
  });

  itensSidebar[2].addEventListener('click', e => {
    e.stopPropagation();
    popups.investimentos.classList.remove('hidden');
    document.body.classList.add('popup-active');
  });

  itensSidebar[3].addEventListener('click', e => {
    e.stopPropagation();
    popups.produtos.classList.remove('hidden');
    document.body.classList.add('popup-active');
  });

  /* -------- Fechar pop-ups (mesmo código, mas agora vale para todos) -------- */
  document.querySelectorAll('.popup-close').forEach(btn => {
    btn.addEventListener('click', () => {
      const alvo = btn.dataset.target;               // ex.: "popup-pagamentos"
      popups[alvo.replace('popup-','')].classList.add('hidden');
      document.body.classList.remove('popup-active');
    });
  });

  /* -------- Clicar fora fecha -------- */
  Object.values(popups).forEach(popup => {
    popup.addEventListener('click', ev => {
      if (ev.target === popup) {
        popup.classList.add('hidden');
        document.body.classList.remove('popup-active');
      }
    });
  });

  /* -------- Seletores de imagem (produtos & pagamentos) -------- */
  const ativaSeletor = (popupId, imgId) => {
    const dots  = document.querySelectorAll(`#${popupId} .selector-dot`);
    const imgEl = document.getElementById(imgId);
    dots.forEach(dot => {
      dot.addEventListener('click', () => {
        dots.forEach(d => d.classList.remove('active'));
        dot.classList.add('active');
        imgEl.src = dot.dataset.src;
      });
    });
  };
  ativaSeletor('popup-produtos',   'produtos-image');
  ativaSeletor('popup-pagamentos', 'pagamentos-image');
});

document.addEventListener('DOMContentLoaded', () => {
  const faces = document.querySelectorAll('.face-item');

  const mensagens = {
    1: {
      title: "Banco GOIA informa:",
      text: "😞 Sentimos muito por não ter atendido às suas expectativas. Estamos ouvindo você e trabalhando para melhorar!",
      icon: "warning"
    },
    2: {
      title: "Banco GOIA informa:",
      text: "😐 Obrigado pelo seu retorno! Vamos buscar tornar sua experiência ainda melhor.",
      icon: "info"
    },
    3: {
      title: "Banco GOIA informa:",
      text: "😊 Que bom que você está satisfeito! Seguimos comprometidos com você.",
      icon: "success"
    },
    4: {
      title: "Banco GOIA informa:",
      text: "😍 Uau! Ficamos muito felizes em saber que você está muito satisfeito. Obrigado pela confiança!",
      icon: "success"
    }
  };

  faces.forEach(face => {
    face.style.cursor = "pointer";
    face.addEventListener('click', () => {
      const nota = parseInt(face.dataset.feedback);
      const msg = mensagens[nota];
      if (msg) {
        Swal.fire({
          title: msg.title,
          text: msg.text,
          icon: msg.icon,
          confirmButtonText: 'Fechar'
        });
      }
    });
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const toggleBtn   = document.getElementById("goibot-toggle");
  const chatFrame   = document.getElementById("goibot-frame");
  const closeBtn    = document.getElementById("goibot-close");

  toggleBtn.addEventListener("click", () => {
    const opened = chatFrame.classList.toggle("visible");
    toggleBtn.classList.toggle("rotated", opened);
    chatFrame.hidden = !opened;            // acessibilidade
  });

  closeBtn.addEventListener("click", () => {
    chatFrame.classList.remove("visible");
    chatFrame.hidden = true;
    toggleBtn.classList.remove("rotated");
  });
});
