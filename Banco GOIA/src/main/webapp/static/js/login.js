// login.js

// Array para saber qual botão (0..4) foi clicado (o 5 é backspace)
let botoesPressionados = [];

// Guardará os pares retornados pelo servidor
let pares = [];

window.onload = function() {
  // ================== 0) VERIFICAR SE HÁ MENSAGEM DE ERRO ==================
  // Fazemos um fetch em "login?acao=verErro" para ver se há loginError na sessão.
  fetch("login?acao=verErro")
    .then(resp => resp.json())
    .then(data => {
      // Ex.: data = { erro: "Senha incorreta!" } ou { erro: null }
      if (data.erro) {
        alert(data.erro);
      }
    })
    .catch(err => console.error("Erro ao verificar erro:", err));

  // ================== 1) CARREGAR OS PARES DE DÍGITOS ==================
  fetch("login?acao=getPares")
    .then(resp => resp.json())
    .then(data => {
      pares = data; // array de { num1, num2 }
      // Preenche o texto dos 5 primeiros botões
      const botoes = document.querySelectorAll('.teclado button');
      for (let i = 0; i < 5; i++) {
        botoes[i].textContent = pares[i].num1 + " ou " + pares[i].num2;
      }
    })
    .catch(err => {
      console.error("Erro ao buscar pares:", err);
      alert("Não foi possível carregar os pares. Recarregue a página.");
    });

  // ================== 2) LÓGICA DE CLIQUE NOS BOTÕES ==================
  const botoes = document.querySelectorAll('.teclado button');
  const senhaInput = document.getElementById('senha');
  const indicesInput = document.getElementById('indicesClicados');
  const form = document.querySelector('form');

  botoes.forEach((botao, index) => {
    botao.addEventListener('click', () => {
      // Se for o botão de backspace (index=5)
      if (index === 5) {
        // Remove o último clique
        botoesPressionados.pop();
        // Remove o último asterisco
        senhaInput.value = senhaInput.value.slice(0, -1);
      } else {
        // Se ainda não chegamos a 6 dígitos
        if (botoesPressionados.length < 6) {
          botoesPressionados.push(index);
          senhaInput.value += "*";
        }
      }
    });
  });

  // ================== 3) AO SUBMETER O FORM ==================
  form.addEventListener('submit', (e) => {
    if (botoesPressionados.length !== 6) {
      e.preventDefault();
      alert("Digite 6 dígitos antes de acessar!");
      return;
    }
    // Convertemos o array para JSON e guardamos no campo hidden
    indicesInput.value = JSON.stringify(botoesPressionados);
    // O form então será submetido normalmente (action="login" method="post")
  });
};
