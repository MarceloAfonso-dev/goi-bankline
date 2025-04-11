// script.js

// Função para embaralhar um array
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
  }
  
  // Exemplo: cria um array com os dígitos de 0 a 9 e embaralha
  const numeros = Array.from({ length: 10 }, (_, i) => i);
  const numerosEmbaralhados = shuffleArray(numeros);
  
  // Seleciona todos os botões do teclado
  const botoesTeclado = document.querySelectorAll('.teclado button');
  
  // Define a senha correta (6 dígitos, por exemplo)
  const senhaCorreta = "123456";
  
  // Atribui pares de números (num1, num2) aos botões, exceto o último (que é backspace)
  botoesTeclado.forEach((botao, index) => {
    // O último botão será o backspace
    if (index < botoesTeclado.length - 1) {
      const num1 = numerosEmbaralhados[index * 2] ?? 0;
      const num2 = numerosEmbaralhados[index * 2 + 1] ?? 0;
      botao.dataset.num1 = num1;
      botao.dataset.num2 = num2;
      botao.textContent = `${num1} ou ${num2}`;
    } else {
      // Botão backspace
      botao.innerHTML = '<img src="../assets/img/backspace.png" alt="botao de apagar">';
    }
  });
  
  // Array para guardar a sequência de botões (índices) que o usuário clica
  let botoesPressionados = [];
  
  // Input onde vamos mostrar os asteriscos
  const senhaInput = document.getElementById('senha');
  
  // Para cada botão, adiciona o evento de click
  botoesTeclado.forEach((botao, index) => {
    botao.addEventListener('click', (event) => {
      event.stopPropagation();
  
      // Se for o botão de backspace...
      const isBackspace = botao.querySelector('img') !== null;
      if (isBackspace) {
        // Remove o último índice (botão) da lista
        botoesPressionados.pop();
        // Remove o último * do input
        senhaInput.value = senhaInput.value.slice(0, -1);
        return;
      }
  
      // Caso não seja backspace, só adiciona se ainda não chegamos a 6 dígitos
      if (botoesPressionados.length < senhaCorreta.length) {
        botoesPressionados.push(index);
        // Exibe * no input de senha
        senhaInput.value += "*";
      }
    });
  
    // Se clicar em um elemento filho (texto ou imagem), também conta
    botao.querySelectorAll('*').forEach(child => {
      child.addEventListener('click', (childEvent) => {
        botao.click();
        childEvent.stopPropagation();
      });
    });
  });
  
  // Botão "acessar"
  const botaoLogar = document.querySelector('.btn-logar');
  botaoLogar.addEventListener('click', () => {
    // Só validamos se o usuário já clicou 6 botões (mesma qtde da senha correta)
    if (botoesPressionados.length !== senhaCorreta.length) {
      alert("Senha incompleta! Digite 6 dígitos.");
      return;
    }
  
    let senhaValida = true;
  
    // Verifica se, para cada posição, o botão pressionado contém o dígito correto
    for (let i = 0; i < senhaCorreta.length; i++) {
      const digitoCorreto = senhaCorreta[i];
      // Índice do botão pressionado na posição i
      const indiceBotao = botoesPressionados[i];
      // Pega os dígitos daquele botão
      const num1 = botoesTeclado[indiceBotao].dataset.num1;
      const num2 = botoesTeclado[indiceBotao].dataset.num2;
  
      // Se o dígito correto não estiver em num1 ou num2, falha
      if (digitoCorreto !== num1 && digitoCorreto !== num2) {
        senhaValida = false;
        break;
      }
    }
  
    if (senhaValida) {
      alert("Senha correta! Acesso permitido.");
      // Aqui você poderia redirecionar para a página principal, etc.
    } else {
      alert("Senha incorreta! Tente novamente.");
      // Limpa tudo para a próxima tentativa
      botoesPressionados = [];
      senhaInput.value = "";
    }
  });
  