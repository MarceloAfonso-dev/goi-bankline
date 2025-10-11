/* cadastro.js – wizard + máscara CPF, validação, step-wizard e pop-up sucesso */
document.addEventListener('DOMContentLoaded', () => {

  /* ───────── ELEMENTOS ───────── */
  const wizardForm = document.getElementById('wizard');
  const cpfInput   = document.getElementById('cpf');
  const cpfMsg     = document.getElementById('cpf-msg');
  const nascInput  = document.getElementById('dataNascimento');

  const steps    = [...document.querySelectorAll('fieldset[data-step]')];
  const progress = document.getElementById('progress');
  const prevBtn  = document.getElementById('prev');
  const nextBtn  = document.getElementById('next');

  /* ───────── ESTADO ───────── */
  let idx = 0;
  let cpfValido = false;

  /* ─────────  CPF  ───────── */
  cpfInput.addEventListener('input', mascaraCpf);
  cpfInput.addEventListener('blur',  ajaxVerificaCpf);

  function mascaraCpf () {
    let v = cpfInput.value.replace(/\D/g,'').slice(0,11);
    if (v.length>9)      v = v.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/,'$1.$2.$3-$4');
    else if (v.length>6) v = v.replace(/(\d{3})(\d{3})(\d{1,3})/,'$1.$2.$3');
    else if (v.length>3) v = v.replace(/(\d{3})(\d{1,3})/,'$1.$2');
    cpfInput.value = v;
    cpfInput.classList.remove('input-erro');  cpfMsg.classList.remove('visivel');
  }

  function ajaxVerificaCpf () {
    const raw = cpfInput.value.replace(/\D/g,'');
    if (raw.length!==11) return;
    fetch('/validarCPF', {
      method:'POST',
      headers:{'Content-Type':'application/x-www-form-urlencoded'},
      body:`cpf=${encodeURIComponent(raw)}&acao=ajaxVerifica`
    })
    .then(r=>r.json())
    .then(({exists})=>{
      cpfValido = !exists;
      if(!cpfValido){
        cpfInput.classList.add('input-erro');
        cpfMsg.classList.add('visivel');
        cpfInput.setCustomValidity('CPF já cadastrado');
      }else{
        cpfInput.setCustomValidity('');
      }
    });
  }

  /* ───────── LIMITE DE DATA ───────── */
  nascInput.addEventListener('change',()=>{
    nascInput.setCustomValidity(
      nascInput.value > '2025-12-31' ?
      'O ano não pode ser maior que 2025' : ''
    );
  });

  /* ───────── WIZARD NAV ───────── */
  nextBtn.addEventListener('click',()=>{
    const fs = steps[idx];
    if(!fs.reportValidity()) return;
    if(idx===0 && !cpfValido){cpfInput.focus();return;}
    if(idx===2){
      const {senha,confirma}=wizardForm;
      if(senha.value!==confirma.value){alert('As senhas não conferem.');return;}
    }
    idx++; render();
  });
  prevBtn.addEventListener('click',()=>{idx--;render();});

  function render(){
    steps.forEach((fs,i)=>fs.style.display = i===idx?'flex':'none');
    progress.value = idx+1;
    prevBtn.disabled = idx===0;
    nextBtn.style.display = idx===steps.length-1?'none':'inline-block';

    if(idx===steps.length-1){
      document.getElementById('resumo').innerHTML = `
        <p><strong>Nome:</strong> ${wizardForm.nome.value}</p>
        <p><strong>CPF:</strong> ${cpfInput.value}</p>
        <p><strong>Nasc.:</strong> ${wizardForm.dataNascimento.value}</p>
        <p><strong>E-mail:</strong> ${wizardForm.email.value}</p>
        <p><strong>Celular:</strong> ${wizardForm.celular.value}</p>
        <p><strong>CEP:</strong> ${wizardForm.cep.value}</p>`;
    }
  }
  render();                                 // inicia passo 1

  /* ───────── SUBMIT ───────── */
  wizardForm.addEventListener('submit',ev=>{
    const raw = cpfInput.value.replace(/\D/g,'');
    if(raw.length!==11 || !cpfValido){
      ev.preventDefault();
      cpfInput.classList.add('input-erro'); cpfMsg.classList.add('visivel');
      return;
    }
    const h=document.createElement('input');
    h.type='hidden';h.name='cpf';h.value=raw;
    wizardForm.appendChild(h); cpfInput.removeAttribute('name');
  });

  /* ───────── POP-UP SUCESSO ───────── */
  function popupSucesso(){
    console.log('✅ Função popupSucesso chamada!');

    // Aplica blur no body
    document.body.classList.add('popup-blur');

    // Mostra o popup customizado
    const popup = document.getElementById('popupSucesso');
    if (popup) {
      console.log('✅ Popup encontrado, exibindo...');
      popup.classList.add('show');

      // Adiciona evento ao botão OK
      const btnOk = document.getElementById('btnOk');
      if (btnOk) {
        btnOk.onclick = function() {
          console.log('✅ Botão OK clicado, redirecionando...');
          // Remove o blur e popup
          document.body.classList.remove('popup-blur');
          popup.classList.remove('show');

          // Redireciona para a página inicial (index)
          setTimeout(() => {
            window.location.href = window.location.origin + '/';
          }, 200);
        };
        console.log('✅ Event listener do botão OK configurado');
      } else {
        console.error('❌ Botão OK não encontrado!');
      }
    } else {
      console.error('❌ Popup não encontrado!');
    }
  }

  // Verifica parâmetro de sucesso na URL
  const urlParams = new URLSearchParams(window.location.search);
  const sucessoParam = urlParams.get('sucesso');

  if(sucessoParam === '1'){
    setTimeout(popupSucesso, 300);
  }

});
