document.addEventListener('DOMContentLoaded', () => {
  const cpfInput = document.getElementById('cpf');
  const cpfForm = document.getElementById('cpfForm');
  const loginButton = document.querySelector('.btn-login');
  const seta = document.querySelector('.seta');

  // Máscara para o campo CPF
  cpfInput.addEventListener('input', () => {
    let value = cpfInput.value.replace(/\D/g, ''); // Remove caracteres não numéricos
    if (value.length > 11) value = value.slice(0, 11); // Limita a 11 dígitos

    // Aplica a máscara
    cpfInput.value = value
      .replace(/(\d{3})(\d)/, '$1.$2') // Adiciona o primeiro ponto
      .replace(/(\d{3})(\d)/, '$1.$2') // Adiciona o segundo ponto
      .replace(/(\d{3})(\d{1,2})$/, '$1-$2'); // Adiciona o traço
  });

  // Simula o clique no botão de login ao clicar na seta
  if (seta && loginButton) {
    seta.addEventListener('click', () => {
      loginButton.click(); // Simula o clique no botão de login
    });
  }

  // Validação e envio do formulário
  cpfForm.addEventListener('submit', (event) => {
    const cpfValue = cpfInput.value.replace(/\D/g, ''); // Remove a máscara para envio
    if (cpfValue.length !== 11) {
      event.preventDefault(); // Impede o envio do formulário
      alert('Por favor, insira um CPF válido com 11 dígitos.');
    } else {
      // Cria um campo oculto para enviar o CPF sem máscara
      const hiddenInput = document.createElement('input');
      hiddenInput.type = 'hidden';
      hiddenInput.name = 'cpf';
      hiddenInput.value = cpfValue; // Apenas os números
      cpfForm.appendChild(hiddenInput);

      // Remove o campo visível para evitar envio duplicado
      cpfInput.removeAttribute('name');
    }
  });
});

document.addEventListener('DOMContentLoaded', () => {
    const video = document.getElementById('bannerVideo');
    const image = document.getElementById('bannerImage');
    const mediaElements = [video, image];
    let currentIndex = 0;
  
    // Função para alternar entre vídeo e imagem
    function switchMedia() {
      mediaElements.forEach((media, index) => {
        media.classList.toggle('active', index === currentIndex);
      });
  
      currentIndex = (currentIndex + 1) % mediaElements.length;
    }
  
    // Inicia o vídeo e alterna a cada 5 segundos
    video.play();
    switchMedia();
    setInterval(switchMedia, 18000);
  });

document.addEventListener('DOMContentLoaded', () => {
  const popup = document.getElementById('popup');
  const popupTitle = document.getElementById('popup-title');
  const popupDescription = document.getElementById('popup-description');
  const popupClose = document.querySelector('.popup-close');
  const popupTriggers = document.querySelectorAll('.popup-trigger');

  // Abre o popup
  popupTriggers.forEach(trigger => {
    trigger.addEventListener('click', (event) => {
      event.preventDefault();
      const title = trigger.getAttribute('data-title');
      const description = trigger.getAttribute('data-description');
      popupTitle.textContent = title;
      popupDescription.textContent = description;
      popup.classList.remove('hidden');
      document.body.classList.add('popup-active');
    });
  });

  // Fecha o popup
  popupClose.addEventListener('click', () => {
    popup.classList.add('hidden');
    document.body.classList.remove('popup-active');
  });

  // Fecha o popup ao clicar fora do conteúdo
  popup.addEventListener('click', (event) => {
    if (event.target === popup) {
      popup.classList.add('hidden');
      document.body.classList.remove('popup-active');
    }
  });
});


document.addEventListener('DOMContentLoaded', () => {
    const faqItems = document.querySelectorAll('.faq-item-coteire3');
  
    faqItems.forEach(item => {
      item.addEventListener('click', () => {
        const answer = item.querySelector('.faq-answer');
        const plusIcon = item.querySelector('.plus-coteire3');
  
        // Alterna a visibilidade da resposta
        if (answer.classList.contains('hidden')) {
          answer.classList.remove('hidden');
          answer.classList.add('active');
          plusIcon.textContent = '-'; // Altera o "+" para "-"
        } else {
          answer.classList.add('hidden');
          answer.classList.remove('active');
          plusIcon.textContent = '+'; // Altera o "-" para "+"
        }
      });
    });
  });