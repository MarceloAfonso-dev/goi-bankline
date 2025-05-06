document.addEventListener('DOMContentLoaded', () => {
    // Máscara CPF
    const cpfInput = document.getElementById('cpf');
    cpfInput.addEventListener('input', () => {
      let v = cpfInput.value.replace(/\D/g, '');
      v = v.replace(/(\d{3})(\d)/, '$1.$2');
      v = v.replace(/(\d{3})(\d)/, '$1.$2');
      v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
      cpfInput.value = v;
    });
  
// formatação de valor em real com milhares
const amountInput = document.getElementById('amount');
amountInput.addEventListener('input', () => {
  // remove tudo que não for dígito
  const clean = amountInput.value.replace(/\D/g, '');
  // transforma em número dividido por 100 (2 casas decimais)
  const value = (parseInt(clean, 10) || 0) / 100;
  // formata para pt-BR com 2 casas decimais
  amountInput.value = value.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
});

  
    // Envio do formulário
    const form = document.getElementById('transfer-form');
    form.addEventListener('submit', (e) => {
      e.preventDefault();
  
      const cpf = cpfInput.value.replace(/\D/g, '');
      const amount = parseFloat(amountInput.value.replace(/\./g, '').replace(',', '.'));
  
      if (cpf.length !== 11) {
        alert('Por favor, insira um CPF válido.');
        return;
      }
      if (isNaN(amount) || amount <= 0) {
        alert('Informe um valor maior que zero.');
        return;
      }
  
      // Aqui faria a chamada à API para processar a transferência…
      alert(`Transferência de R$ ${amountInput.value} para CPF ${cpfInput.value} realizada com sucesso!`);
      form.reset();
    });
  });
  