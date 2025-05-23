/* ================================================================
 * GOI Bank – Landing Page JS
 * ================================================================
 * - Máscara + envio de CPF no login
 * - Banner: alterna vídeo / imagem
 * - Pop-ups de serviços
 * - FAQ toggler
 * - Simulador de Cheque Especial (seguro contra NaN)
 * - NPS SweetAlert
 * ================================================================ */

document.addEventListener('DOMContentLoaded', () => {

  /* --------------------------------------------------------------*
   * 1) LOGIN / CPF HEADER
   * --------------------------------------------------------------*/
  const cpfLogin   = document.getElementById('cpf');
  const formLogin  = document.getElementById('cpfForm');
  const btnLogin   = document.querySelector('.btn-login');
  const setaLogin  = document.querySelector('.seta');

  const mascaraCPF = v => v
        .replace(/\D/g,'').slice(0,11)
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');

  cpfLogin.addEventListener('input', e =>
      e.target.value = mascaraCPF(e.target.value));

  /* seta “enter” */
  setaLogin?.addEventListener('click', ()=> btnLogin?.click());

  /* envia CPF limpo */
  formLogin.addEventListener('submit', ev=>{
      const cpfNum = cpfLogin.value.replace(/\D/g,'');
      if (cpfNum.length!==11){
          ev.preventDefault();
          alert('CPF deve ter 11 dígitos.');
          return;
      }
      const hidden = document.createElement('input');
      hidden.type  = 'hidden';
      hidden.name  = 'cpf';        // o back-end espera “cpf”
      hidden.value = cpfNum;
      formLogin.appendChild(hidden);
      cpfLogin.removeAttribute('name');   // evita duplicidade
  });

  /* --------------------------------------------------------------*
   * 2) BANNER (vídeo + imagem a cada 18 s)
   * --------------------------------------------------------------*/
  const vid   = document.getElementById('bannerVideo');
  const img   = document.getElementById('bannerImage');
  const media = [vid, img];
  let idx     = 0;

  const switchMedia = () => {
       media.forEach((m,i)=>m.classList.toggle('active', i===idx));
       idx = (idx+1) % media.length;
  };
  vid?.play();
  switchMedia();
  setInterval(switchMedia, 18_000);

  /* --------------------------------------------------------------*
   * 3) POP-UP genérico (cards “Saiba mais”)
   * --------------------------------------------------------------*/
  const popup        = document.getElementById('popup');
  const popupTitle   = document.getElementById('popup-title');
  const popupDesc    = document.getElementById('popup-description');
  const popupClose   = document.querySelector('.popup-close');

  document.querySelectorAll('.popup-trigger').forEach(a=>{
      a.addEventListener('click', ev=>{
          ev.preventDefault();
          popupTitle.textContent = a.dataset.title  || '';
          popupDesc.textContent  = a.dataset.description || '';
          popup.classList.remove('hidden');
          document.body.classList.add('popup-active');
      });
  });
  popupClose?.addEventListener('click', fecharPopup);
  popup.addEventListener('click', e => { if (e.target === popup) fecharPopup(); });
  function fecharPopup(){
      popup.classList.add('hidden');
      document.body.classList.remove('popup-active');
  }

  /* --------------------------------------------------------------*
   * 4) FAQ
   * --------------------------------------------------------------*/
  document.querySelectorAll('.faq-item-coteire3').forEach(item=>{
      item.addEventListener('click', ()=>{
          const ans  = item.querySelector('.faq-answer');
          const plus = item.querySelector('.plus-coteire3');
          const open = ans.classList.toggle('active');
          ans.classList.toggle('hidden', !open);
          plus.textContent = open ? '-' : '+';
      });
  });

  /* --------------------------------------------------------------*
   * 5) SIMULADOR DE CHEQUE ESPECIAL
   * --------------------------------------------------------------*/
  const cpfSim   = document.getElementById('cpfSimulador');
  const rendaInp = document.getElementById('rendaMensal');
  const btnSim   = document.getElementById('btnSimularCheque');
  const saidaSim = document.getElementById('resultadoSimulacao');

  const reais = n => new Intl.NumberFormat('pt-BR',{
                      style:'currency',currency:'BRL'}).format(n);

  cpfSim?.addEventListener('input', e=> e.target.value = mascaraCPF(e.target.value));

  rendaInp?.addEventListener('input', e=>{
      const n = e.target.value.replace(/\D/g,'');
      if(!n){ e.target.value=''; return; }
      e.target.value = reais((n/100).toFixed(2));
  });

  btnSim?.addEventListener('click', ()=>{
      const cpfNum   = (cpfSim?.value || '').replace(/\D/g,'');
      const rendaNum = parseFloat( (rendaInp?.value || '').replace(/\D/g,'') ) / 100;

      if (cpfNum.length !== 11){
          exibirErro('CPF inválido.');
          return;
      }
      if (!rendaNum){
          exibirErro('Informe sua renda mensal.');
          return;
      }

      fetch('/api/simulador/cheque',{
          method :'POST',
          headers:{'Content-Type':'application/json'},
          body   : JSON.stringify({ cpf: cpfNum, rendaMensal: rendaNum })
      })
      .then(async r=>{
          const j = await r.json();
          if (!r.ok) throw new Error(j.erro || 'Falha na simulação');
          return j;
      })
      .then(d=>{
          if (Number.isFinite(d.rendaMensal) && Number.isFinite(d.limite)){
              saidaSim.className = 'resultado-simulador sucesso';
              saidaSim.innerHTML =
                 `<p>Com uma renda de <strong>${reais(d.rendaMensal)}</strong>,</p>
                  <p>seu limite estimado é <strong>${reais(d.limite)}</strong>.</p>`;
          } else {
              throw new Error('Resposta inesperada.');
          }
      })
      .catch(err => exibirErro(err.message));
  });

  function exibirErro(msg){
      saidaSim.className = 'resultado-simulador erro';
      saidaSim.textContent = msg;
  }

  /* --------------------------------------------------------------*
   * 6) NPS – SweetAlert
   * --------------------------------------------------------------*/
  const msgNps = {
       1:["Banco GOI informa:",
          "😞 Sentimos muito por não atender às suas expectativas. Estamos ouvindo você e trabalhando para melhorar!","warning"],
       2:["Banco GOI informa:",
          "😐 Obrigado pelo retorno! Vamos buscar tornar sua experiência ainda melhor.","info"],
       3:["Banco GOI informa:",
          "😊 Que bom que você está satisfeito! Seguimos comprometidos com você.","success"],
       4:["Banco GOI informa:",
          "😍 Uau! Muito obrigado pela confiança!","success"]
  };

  document.querySelectorAll('.face-item').forEach(face=>{
      face.addEventListener('click', ()=>{
          const nota         = +face.dataset.feedback;
          const [t,txt,ico]  = msgNps[nota] || [];
          if(t) Swal.fire({title:t, text:txt, icon:ico, confirmButtonText:'Fechar'});
      });
  });

});
