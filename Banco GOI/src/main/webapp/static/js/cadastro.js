// cadastro.js – controle do wizard de cadastro GOI Bank

document.addEventListener('DOMContentLoaded', () => {
  /* ========================= UTIL – máscara CPF ========================= */
  const cpfInput   = document.getElementById('cpf');
  const wizardForm = document.getElementById('wizard');

  cpfInput.addEventListener('input', () => {
    let v = cpfInput.value.replace(/\D/g, '').slice(0, 11);
    if (v.length > 9)      v = v.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
    else if (v.length > 6) v = v.replace(/(\d{3})(\d{3})(\d{1,3})/,  '$1.$2.$3');
    else if (v.length > 3) v = v.replace(/(\d{3})(\d{1,3})/,          '$1.$2');
    cpfInput.value = v;
  });

  /* ========================= WIZARD ========================= */
  const steps    = Array.from(document.querySelectorAll('fieldset[data-step]'));
  const progress = document.getElementById('progress');
  const prevBtn  = document.getElementById('prev');
  const nextBtn  = document.getElementById('next');
  let idx        = 0;                              // passo atual (0‑based)

  function render() {
    steps.forEach((fs, i) => fs.classList.toggle('active', i === idx));
    progress.value = idx + 1;
    prevBtn.disabled     = idx === 0;
    nextBtn.style.display = idx === steps.length - 1 ? 'none' : 'inline-block';

    // resumo final
    if (idx === steps.length - 1) {
      const r = document.getElementById('resumo');
      r.innerHTML = `<p><strong>Nome:</strong> ${wizardForm.nome.value}</p>
                     <p><strong>CPF:</strong> ${cpfInput.value}</p>
                     <p><strong>Nascimento:</strong> ${wizardForm.dataNascimento.value}</p>
                     <p><strong>E‑mail:</strong> ${wizardForm.email.value}</p>
                     <p><strong>Celular:</strong> ${wizardForm.celular.value}</p>
                     <p><strong>CEP:</strong> ${wizardForm.cep.value}</p>`;
    }
  }

  nextBtn.addEventListener('click', () => {
    const currentFs = steps[idx];
    if (!currentFs.reportValidity()) return;      // valida campos visíveis

    // validação de senhas no passo 3 (idx 2)
    if (idx === 2) {
      const { senha, confirma } = wizardForm;
      const regex = /^\d{6}$/;
      if (!regex.test(senha.value)) {
        alert('A senha deve conter exatamente 6 dígitos numéricos.');
        return;
      }
      if (senha.value !== confirma.value) {
        alert('As senhas não conferem.');
        return;
      }
    }
    idx++;
    render();
  });

  prevBtn.addEventListener('click', () => { idx--; render(); });

  wizardForm.addEventListener('submit', ev => {
    const rawCpf = cpfInput.value.replace(/\D/g, '');
    if (rawCpf.length !== 11) {
      ev.preventDefault();
      alert('CPF deve ter 11 dígitos.');
      return;
    }
    const hidden = document.createElement('input');
    hidden.type  = 'hidden';
    hidden.name  = 'cpf';
    hidden.value = rawCpf;
    wizardForm.appendChild(hidden);
    cpfInput.removeAttribute('name');
  });

  // inicia wizard
  render();
});
