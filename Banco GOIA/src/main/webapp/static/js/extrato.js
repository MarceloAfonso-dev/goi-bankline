document.addEventListener("DOMContentLoaded", () => {

  /* ======================================================
     1) SALDO ATUdocument.addEventListener("DOMContentLoaded", () => {
  // botão de "Sair"
  const logoutBtn = document.querySelector(".sair");
  logoutBtn.style.cursor = "pointer";
  logoutBtn.addEventListener("click", () => {
    if (window.urlManager) {
      window.urlManager.logout();
    } else {
      window.location.href = '../index.html';
    }
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
        window.location.href = 'home.html';
      }
    });
  });
});alho do extrato)
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
    if (!iso || typeof iso !== 'string') {
      return 'Data inválida';
    }

    const parts = iso.split("-");
    if (parts.length !== 3) {
      return 'Data inválida';
    }

    const [y, m, d] = parts;
    const year = parseInt(y, 10);
    const month = parseInt(m, 10);
    const day = parseInt(d, 10);

    // Verifica se os valores são números válidos
    if (isNaN(year) || isNaN(month) || isNaN(day)) {
      return 'Data inválida';
    }

    // Verifica se a data é válida
    const date = new Date(year, month - 1, day);
    if (date.getFullYear() !== year || date.getMonth() !== month - 1 || date.getDate() !== day) {
      return 'Data inválida';
    }

    return date.toLocaleDateString("pt-BR", { day:"numeric", month:"long" });
  };

  /* ======================================================
     3) RENDERIZA LISTA
     ====================================================== */
  const wrapper = document.getElementById("movimentacoesWrapper");
  if (!wrapper) return;               // segurança

  function renderMovimentacoes(movs) {
    wrapper.innerHTML = "";           // limpa antes de renderizar

    if (!movs || movs.length === 0) {
      wrapper.innerHTML = "<p style='padding:20px; text-align:center;'>Nenhuma movimentação encontrada.</p>";
      return;
    }

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
  // === botão de "Sair" ===
  const logoutBtn = document.querySelector(".sair");
  logoutBtn.style.cursor = "pointer";
  logoutBtn.addEventListener("click", () => {
    if (window.urlManager) {
      window.urlManager.logout();
    } else {
      window.location.href = '../index.html';
    }
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
        window.location.href = 'home.html';
      }
    });
  });
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
