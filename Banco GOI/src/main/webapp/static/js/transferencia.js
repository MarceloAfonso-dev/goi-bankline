document.addEventListener('DOMContentLoaded', () => {
  // === 0) buscar agência/conta do usuário logado ===
  fetch("/conta")
    .then(res => {
      if (!res.ok) throw new Error("Não autenticado");
      return res.json();
    })
    .then(data => {
      const agEl    = document.getElementById("agencia");
      const ccEl    = document.getElementById("conta");
      if (agEl) agEl.textContent = `ag: ${data.agencia}`;
      if (ccEl) ccEl.textContent = `c/c: ${data.conta}`;
    })
    .catch(err => {
      console.warn("Não foi possível carregar agência/conta:", err);
    });

  // 1) busca saldo atual
  fetch('saldo')
    .then(resp => resp.json())
    .then(data => {
      const saldoEl = document.getElementById('saldoAtual');
      if (data.error) {
        saldoEl.textContent = data.error;
      } else {
        const v = parseFloat(data.saldo);
        saldoEl.textContent = v.toLocaleString('pt-BR', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        });
      }
    })
    .catch(err => console.error('Erro ao carregar saldo:', err));

  const cpfInput    = document.getElementById('cpfDestino');
  const amountInput = document.getElementById('amount');
  const mensagemEl  = document.getElementById('mensagem');
  const form        = document.getElementById('transfer-form');

  // 2) máscara de CPF
  cpfInput.addEventListener('input', () => {
    let v = cpfInput.value.replace(/\D/g, '');
    if (v.length > 11) v = v.slice(0, 11);
    cpfInput.value = v
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
  });

  // 3) formatação de valor em Real com milhares
  amountInput.addEventListener('input', () => {
    const clean = amountInput.value.replace(/\D/g, '');
    const value = (parseInt(clean, 10) || 0) / 100;
    amountInput.value = value.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  });

  // 4) captura mensagem de redirect (?msg=)
  const params = new URLSearchParams(window.location.search);
  if (params.has('msg')) {
    mensagemEl.textContent = params.get('msg');
  }

  // 5) validação AJAX de CPF e envio do formulário
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    mensagemEl.textContent = '';

    // CPF sem máscara
    const cpfRaw = cpfInput.value.replace(/\D/g, '');
    if (cpfRaw.length !== 11) {
      mensagemEl.textContent = 'Por favor, insira um CPF válido com 11 dígitos.';
      return;
    }

    // verifica existência no banco
    try {
      const resp = await fetch('validarCPF?acao=ajaxVerifica', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `cpf=${encodeURIComponent(cpfRaw)}`
      });
      const { exists } = await resp.json();
      if (!exists) {
        mensagemEl.textContent = 'CPF de destino não encontrado.';
        return;
      }
    } catch (err) {
      mensagemEl.textContent = 'Erro ao validar CPF.';
      return;
    }

    // 6) prepara campo oculto com cpfDestino
    let hidden = form.querySelector('input[name="cpfDestino"]');
    if (!hidden) {
      hidden = document.createElement('input');
      hidden.type = 'hidden';
      hidden.name = 'cpfDestino';
      form.appendChild(hidden);
    }
    hidden.value = cpfRaw;

    // evita envio do campo mascarado
    cpfInput.removeAttribute('name');

    // 7) envia o form para o servlet
    form.submit();
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
