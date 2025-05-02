document.addEventListener('DOMContentLoaded', function () {
    const etapa1 = document.getElementById('informacoesIniciais');
    const etapa2 = document.getElementById('informacoesComplementares');
  
    const btnEtapa1 = document.getElementById('submitBtn');
    const btnEtapa2 = document.getElementById('submitComplementar');
  
    function validarEtapa1() {
      const cpf = document.getElementById('cpf').value.trim();
      const email = document.getElementById('email').value.trim();
      const celular = document.getElementById('celular').value.trim();
  
      const regexCpf = /^\d{11}$/;
      const regexEmail = /^[a-zA-Z0-9._%+-]+@(gmail|outlook)\.com$/;
      const regexCelular = /^\(\d{2}\)\s?\d{4,5}-?\d{4}$/;
  
      if (!regexCpf.test(cpf)) {
        alert("O CPF deve conter exatamente 11 números (somente dígitos).");
        return false;
      }
  
      if (!regexEmail.test(email)) {
        alert("O e-mail deve terminar com @gmail.com ou @outlook.com.");
        return false;
      }
  
      if (!regexCelular.test(celular)) {
        alert("O celular deve incluir o DDD entre parênteses. Exemplo: (11) 91234-5678");
        return false;
      }
  
      return true;
    }
  
    btnEtapa1.addEventListener('click', function (e) {
      e.preventDefault();
  
      if (validarEtapa1()) {
        etapa1.classList.add('hidden');
        etapa2.classList.remove('hidden');
      }
    });
  
    btnEtapa2.addEventListener('click', function (e) {
      e.preventDefault();
      etapa2.classList.add('hidden');
      document.getElementById('informacoesRG').classList.remove('hidden');
    });
  });
  