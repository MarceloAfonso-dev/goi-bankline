/* ---------------------------------------------------------------
   TRANSFERÊNCIA – donut de loading, submit tradicional, redirect
   --------------------------------------------------------------- */
document.addEventListener('DOMContentLoaded', () => {

  /* ===================== referências ===================== */
  const agEl     = document.getElementById('agencia');
  const ccEl     = document.getElementById('conta');
  const saldoEl  = document.getElementById('saldoAtual');
  const form     = document.getElementById('transfer-form');
  const cpfInput = document.getElementById('cpfDestino');
  const valInput = document.getElementById('amount');   // <input name="valor">
  const msgEl    = document.getElementById('mensagem');

  /* overlay */
  const overlay  = document.getElementById('loadingOverlay');
  const pctEl    = document.getElementById('loaderPercent');
  const progEl   = document.querySelector('.ring .progress');
  let pctTimer;

  /* ===================== cabeçalho ===================== */
  fetch('/conta')
    .then(r => r.json())
    .then(d => {
      agEl.textContent = `ag: ${d.agencia}`;
      ccEl.textContent = `c/c: ${d.conta}`;
    });

  fetch('/saldo')
    .then(r => r.json())
    .then(d => {
      saldoEl.textContent = parseFloat(d.saldo)
        .toLocaleString('pt-BR', { minimumFractionDigits: 2 });
    });

  /* ===================== máscaras ===================== */
  cpfInput.addEventListener('input', () => {
    let v = cpfInput.value.replace(/\D/g, '').slice(0, 11);
    cpfInput.value = v.replace(/(\d{3})(\d)/,        '$1.$2')
                      .replace(/(\d{3})(\d)/,        '$1.$2')
                      .replace(/(\d{3})(\d{1,2})$/,  '$1-$2');
  });

  valInput.addEventListener('input', () => {
    const n = (parseInt(valInput.value.replace(/\D/g, ''), 10) || 0) / 100;
    valInput.value = n.toLocaleString('pt-BR', { minimumFractionDigits: 2 });
  });

  /* ===================== overlay helpers ===================== */
  function showOverlay () {
    progEl.style.animation = 'none'; void progEl.offsetWidth;
    progEl.style.animation = 'donut 3s linear forwards'; // Reduzido para 3s para melhor UX
    pctEl.textContent = '0 %';
    overlay.classList.remove('hidden');

    let p = 0;
    pctTimer = setInterval(() => {
      p += 1; // Incremento menor para animação mais suave
      if (p >= 100) {
        p = 100;
        clearInterval(pctTimer);
        pctEl.textContent = `${p} %`;

        // Pequeno delay após atingir 100% para melhor experiência
        setTimeout(() => {
          // A animação completou, agora pode prosseguir com o submit
          if (window.pendingFormSubmit) {
            window.pendingFormSubmit();
          }
        }, 500);
        return;
      }
      pctEl.textContent = `${p} %`;
    }, 30); // Intervalo reduzido para animação mais suave (3000ms / 100 = 30ms)
  }

  function hideOverlay() {
    overlay.classList.add('hidden');
    clearInterval(pctTimer);
    window.pendingFormSubmit = null;
  }

  window.addEventListener('load', () => hideOverlay());

  /* ===================== exibe mensagem do servlet ===================== */
  (function exibeMensagem () {
    const raw = new URLSearchParams(location.search).get('msg');
    if (!raw) return;                     // não veio nada → não mostra
    const texto = decodeURIComponent(raw.replace(/\+/g, ' '));
    msgEl.textContent = texto;
    msgEl.style.color =
      /sucesso/i.test(texto) ? 'green' : 'red';
  })();

  /* ===================== envio (submit tradicional) ===================== */
  form.addEventListener('submit', ev => {
    ev.preventDefault();
    msgEl.textContent = ''; msgEl.style.color = 'red';

    /* valida CPF */
    const cpfRaw = cpfInput.value.replace(/\D/g, '');
    if (cpfRaw.length !== 11) {
      msgEl.textContent = 'CPF inválido'; return;
    }

    /* valida valor – formato 1.234,56 */
    if (!/^\d{1,3}(?:\.\d{3})*,\d{2}$/.test(valInput.value)) {
      msgEl.textContent = 'Valor inválido'; return;
    }

    /* campo oculto com CPF “limpo” */
    let hidden = form.querySelector('input[name="cpfDestino"]');
    if (!hidden) {
      hidden = document.createElement('input');
      hidden.name = 'cpfDestino';
      hidden.type = 'hidden';
      form.appendChild(hidden);
    }
    hidden.value = cpfRaw;

    /* remove name do campo mascarado para evitar duplicidade */
    cpfInput.removeAttribute('name');

    showOverlay();      // donut aparece

    // Aguarda a animação completar 100% antes de prosseguir com o submit
    window.pendingFormSubmit = () => {
      form.submit();      // servlet processa e redireciona para /transferencia?msg=...
    };
  });

  /* ===================== UX extra: sair e logo ===================== */
  const logoutBtn = document.querySelector('.sair');
  if (logoutBtn) {
    logoutBtn.style.cursor = 'pointer';
    logoutBtn.addEventListener('click', () => {
      if (window.urlManager) {
        window.urlManager.logout();
      } else {
        window.location.href = '/';
      }
    });
  }

  const logoImg  = document.querySelector('header .icone-goia');
  const logoText = document.querySelector('header h1');
  [logoImg, logoText].forEach(el => {
    if (!el) return;
    el.style.cursor = 'pointer';
    el.addEventListener('click', () => {
      if (window.urlManager) {
        window.urlManager.goHome();
      } else {
        window.location.href = '/templates/home.html';
      }
    });
  });

}); /* fim do DOMContentLoaded */

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
