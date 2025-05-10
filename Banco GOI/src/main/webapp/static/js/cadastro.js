document.addEventListener('DOMContentLoaded', () => {
  const cpfInput = document.getElementById('cpf');
  const cadastroForm = document.querySelector('form[action="/cadastro"]');

  if (cpfInput && cadastroForm) {
    cadastroForm.addEventListener('submit', (event) => {
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
        cadastroForm.appendChild(hiddenInput);

        // Remove o campo visível para evitar envio duplicado
        cpfInput.removeAttribute('name');
      }
    });
  }
});