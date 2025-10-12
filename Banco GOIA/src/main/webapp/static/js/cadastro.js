/* cadastro.js – wizard + máscara CPF, validação, step-wizard e pop-up sucesso */
document.addEventListener('DOMContentLoaded', () => {

  /* ───────── ELEMENTOS ───────── */
  const wizardForm = document.getElementById('wizard');
  const cpfInput   = document.getElementById('cpf');
  const cpfMsg     = document.getElementById('cpf-msg');
  const nascInput  = document.getElementById('dataNascimento');
  const nascMsg    = document.getElementById('nasc-msg');
  const emailInput = document.getElementById('email');
  const emailMsg   = document.getElementById('email-msg');
  const senhaInput = document.getElementById('senha');
  const senhaMsg   = document.getElementById('senha-msg');

  const steps    = [...document.querySelectorAll('fieldset[data-step]')];
  const progress = document.getElementById('progress');
  const prevBtn  = document.getElementById('prev');
  const nextBtn  = document.getElementById('next');

  /* ───────── ESTADO ───────── */
  let idx = 0;
  let cpfValido = false;
  let nascimentoValido = false;
  let emailValido = false;
  let senhaValida = false;

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

  /* ───────── VALIDAÇÃO DE IDADE (18+) ───────── */
  nascInput.addEventListener('change', validarIdade);
  nascInput.addEventListener('blur', validarIdade);

  function validarIdade() {
    if (!nascInput.value) {
      nascimentoValido = false;
      return;
    }

    const hoje = new Date();
    const nascimento = new Date(nascInput.value);
    const idade = hoje.getFullYear() - nascimento.getFullYear();
    const mesAtual = hoje.getMonth();
    const mesNasc = nascimento.getMonth();
    
    // Ajusta se ainda não fez aniversário este ano
    const idadeExata = (mesAtual < mesNasc || (mesAtual === mesNasc && hoje.getDate() < nascimento.getDate())) 
      ? idade - 1 
      : idade;

    nascimentoValido = idadeExata >= 18;
    
    if (!nascimentoValido) {
      nascInput.classList.add('input-erro');
      nascMsg.classList.add('visivel');
      nascInput.setCustomValidity('Você deve ter pelo menos 18 anos');
    } else {
      nascInput.classList.remove('input-erro');
      nascMsg.classList.remove('visivel');
      nascInput.setCustomValidity('');
    }
  }

  /* ───────── VALIDAÇÃO DE EMAIL ───────── */
  emailInput.addEventListener('input', validarEmail);
  emailInput.addEventListener('blur', validarEmail);

  function validarEmail() {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    emailValido = emailRegex.test(emailInput.value);
    
    if (!emailValido && emailInput.value.length > 0) {
      emailInput.classList.add('input-erro');
      emailMsg.classList.add('visivel');
      emailInput.setCustomValidity('E-mail deve estar em formato válido');
    } else {
      emailInput.classList.remove('input-erro');
      emailMsg.classList.remove('visivel');
      emailInput.setCustomValidity('');
    }
  }

  /* ───────── VALIDAÇÃO DE SENHA (APENAS NÚMEROS) ───────── */
  senhaInput.addEventListener('input', validarSenha);
  senhaInput.addEventListener('blur', validarSenha);

  function validarSenha() {
    // Remove qualquer caractere que não seja número
    let valor = senhaInput.value.replace(/\D/g, '');
    senhaInput.value = valor;
    
    senhaValida = valor.length === 6 && /^\d{6}$/.test(valor);
    
    if (!senhaValida && valor.length > 0) {
      senhaInput.classList.add('input-erro');
      senhaMsg.classList.add('visivel');
      senhaInput.setCustomValidity('A senha deve conter apenas números (6 dígitos)');
    } else {
      senhaInput.classList.remove('input-erro');
      senhaMsg.classList.remove('visivel');
      senhaInput.setCustomValidity('');
    }
  }

  /* ───────── WIZARD NAV ───────── */
  nextBtn.addEventListener('click',()=>{
    const fs = steps[idx];
    if(!fs.reportValidity()) return;
    
    // Validações específicas por passo
    if(idx === 0) {
      if(!cpfValido) {
        cpfInput.focus();
        return;
      }
      if(!nascimentoValido) {
        nascInput.focus();
        return;
      }
    }
    
    if(idx === 1) {
      if(!emailValido) {
        emailInput.focus();
        return;
      }
    }
    
    if(idx === 2) {
      if(!senhaValida) {
        senhaInput.focus();
        return;
      }
      const {senha, confirma} = wizardForm;
      if(senha.value !== confirma.value) {
        alert('As senhas não conferem.');
        return;
      }
    }
    
    idx++; 
    render();
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
    // Validação final de todos os campos
    const raw = cpfInput.value.replace(/\D/g,'');
    
    // Verifica CPF
    if(raw.length!==11 || !cpfValido){
      ev.preventDefault();
      cpfInput.classList.add('input-erro'); 
      cpfMsg.classList.add('visivel');
      alert('CPF inválido ou já cadastrado.');
      return;
    }
    
    // Verifica idade
    if(!nascimentoValido){
      ev.preventDefault();
      nascInput.classList.add('input-erro');
      nascMsg.classList.add('visivel');
      alert('Você deve ter pelo menos 18 anos para criar uma conta.');
      return;
    }
    
    // Verifica email
    if(!emailValido){
      ev.preventDefault();
      emailInput.classList.add('input-erro');
      emailMsg.classList.add('visivel');
      alert('E-mail deve estar em formato válido.');
      return;
    }
    
    // Verifica senha
    if(!senhaValida){
      ev.preventDefault();
      senhaInput.classList.add('input-erro');
      senhaMsg.classList.add('visivel');
      alert('A senha deve conter apenas números (6 dígitos).');
      return;
    }
    
    // Se chegou até aqui, tudo válido - prepara dados para envio
    const h=document.createElement('input');
    h.type='hidden';h.name='cpf';h.value=raw;
    wizardForm.appendChild(h); cpfInput.removeAttribute('name');
  });

  /* ───────── POP-UP SUCESSO ───────── */
  function popupSucesso(){
    console.log('🎉 === POPUP SUCESSO INICIADO ===');

    // Mostra o popup customizado (usa as classes corretas do CSS)
    const popup = document.getElementById('popupSucesso');
    console.log('🔍 Popup element:', popup);
    
    if (popup) {
      console.log('✅ Popup encontrado, aplicando classes...');
      
      // Força exibição do popup
      popup.style.display = 'flex';
      popup.style.visibility = 'visible';
      popup.style.opacity = '1';
      
      // Aplica as classes corretas conforme o CSS
      document.body.classList.add('popup-blur');
      popup.classList.add('show');
      
      console.log('✅ Classes aplicadas - popup deve estar visível agora');
      console.log('🔍 Popup classes:', popup.className);
      console.log('🔍 Body classes:', document.body.className);

      // Adiciona evento ao botão OK
      const btnOk = document.getElementById('btnOk');
      console.log('🔍 Botão OK:', btnOk);
      
      if (btnOk) {
        btnOk.onclick = function() {
          console.log('✅ Botão OK clicado, fechando popup...');
          
          // Remove as classes do popup
          document.body.classList.remove('popup-blur');
          popup.classList.remove('show');
          popup.style.display = 'none';

          // Redireciona para a página inicial após uma pequena animação
          setTimeout(() => {
            console.log('🏠 Redirecionando para home...');
            if (window.urlManager) {
              window.urlManager.navigateTo('/');
            } else {
              window.location.href = '/';
            }
          }, 300);
        };
        console.log('✅ Event listener do botão OK configurado');
      } else {
        console.error('❌ Botão OK não encontrado no DOM!');
      }
    } else {
      console.error('❌ Elemento #popupSucesso não encontrado no DOM!');
      // Fallback - usa alert simples
      alert('🎉 Conta criada com sucesso!\nSeu bônus de R$ 1.500 foi creditado.\nRedirecionando para o início...');
      setTimeout(() => {
        window.location.href = '/';
      }, 1000);
    }
  }

  // Função para verificar sucesso via AJAX (método confiável)
  function verificarSucessoCadastro() {
    console.log('🔄 Verificando sucesso via AJAX...');
    alert('🔄 TESTE: Função verificarSucessoCadastro chamada!');
    
    fetch('/cadastro?acao=verificarSucesso', {
      method: 'GET',
      headers: {'Content-Type': 'application/json'}
    })
    .then(r => r.json())
    .then(({sucesso}) => {
      console.log('🔍 Resposta AJAX - sucesso:', sucesso);
      
      if (sucesso) {
        console.log('🎉 === SUCESSO DETECTADO VIA AJAX ===');
        console.log('✅ Servidor confirmou cadastro bem-sucedido');
        
        // Verifica elementos do DOM
        const popup = document.getElementById('popupSucesso');
        const btnOk = document.getElementById('btnOk');
        
        console.log('🔍 Popup existe?', !!popup);
        console.log('🔍 Botão OK existe?', !!btnOk);
        
        setTimeout(() => {
          console.log('� Executando popupSucesso agora!');
          popupSucesso();
        }, 300);
      } else {
        console.log('ℹ️ Nenhum cadastro pendente na sessão');
      }
    })
    .catch(e => {
      console.log('⚠️ Erro ao verificar sucesso via AJAX:', e);
      
      // Fallback: verifica parâmetro URL
      const urlParams = new URLSearchParams(window.location.search);
      const sucessoParam = urlParams.get('sucesso');
      
      if(sucessoParam === '1'){
        console.log('🎉 === FALLBACK: SUCESSO VIA URL ===');
        setTimeout(popupSucesso, 300);
      }
    });
  }

  // Debug inicial
  console.log('🔍 URL atual:', window.location.href);
  console.log('🔍 Domínio atual:', window.location.hostname);
  
  // TESTE IMEDIATO - deve aparecer sempre
  alert('🔍 TESTE: JavaScript do cadastro carregado! Verificando sucesso...');
  
  // SEMPRE verifica via AJAX ao carregar a página
  setTimeout(verificarSucessoCadastro, 500);

});
