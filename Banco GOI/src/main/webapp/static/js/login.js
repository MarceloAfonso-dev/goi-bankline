// login.js

// Array para saber qual botão (0..4) foi clicado (o 5 é backspace)
let botoesPressionados = [];

// Guardará os pares retornados pelo servidor
let pares = []; // ex.: [ {num1:"7", num2:"3"}, {num1:"0", num2:"9"}, ... ]

window.onload = function() {
  // 1) Carrega os pares via AJAX GET ?acao=getPares
  fetch("login?acao=getPares")
    .then(resp => resp.json())
    .then(data => {
      pares = data; // array de objetos {num1, num2}
      // Preenche o texto dos 5 primeiros botões
      const botoes = document.querySelectorAll('.teclado button');
      for (let i = 0; i < 5; i++) {
        const p = pares[i];
        // Ex.: p.num1=7, p.num2=3 => "7 ou 3"
        botoes[i].textContent = p.num1 + " ou " + p.num2;
      }
    })
    .catch(err => {
      console.error("Erro ao buscar pares aleatórios: ", err);
      alert("Não foi possível carregar os pares. Recarregue a página.");
    });

  // 2) Configura o clique nos botões
  const botoes = document.querySelectorAll('.teclado button');
  const senhaInput = document.getElementById('senha');
  const indicesInput = document.getElementById('indicesClicados');
  const form = document.querySelector('form');

  botoes.forEach((botao, index) => {
    botao.addEventListener('click', () => {
      // Se for o último (backspace, index=5)
      if (index === 5) {
        // Apagar o último clique
        botoesPressionados.pop();
        senhaInput.value = senhaInput.value.slice(0, -1);
      } else {
        // Se ainda não tem 6 cliques
        if (botoesPressionados.length < 6) {
          botoesPressionados.push(index);
          senhaInput.value += "*";
        }
      }
    });
  });

  // 3) Ao submeter o form, enviamos o array de índices
  form.addEventListener('submit', (e) => {
    if (botoesPressionados.length !== 6) {
      e.preventDefault();
      alert("Digite 6 dígitos!");
      return;
    }
    // Armazena em JSON no campo hidden
    indicesInput.value = JSON.stringify(botoesPressionados);
  });
};
