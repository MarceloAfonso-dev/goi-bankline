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