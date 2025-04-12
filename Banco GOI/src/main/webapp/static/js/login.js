// ======== 1) Seleciona os botões do teclado ========
const botoesTeclado = document.querySelectorAll('.teclado button');

// Precisamos identificar manualmente quais pares cada botão representa.
// (Índice 0 => "1 ou 2", índice 1 => "3 ou 4", etc.)
// O último botão (índice 5) é o backspace, então não tem par.
botoesTeclado[0].dataset.num1 = "1";
botoesTeclado[0].dataset.num2 = "2";

botoesTeclado[1].dataset.num1 = "3";
botoesTeclado[1].dataset.num2 = "4";

botoesTeclado[2].dataset.num1 = "5";
botoesTeclado[2].dataset.num2 = "6";

botoesTeclado[3].dataset.num1 = "7";
botoesTeclado[3].dataset.num2 = "8";

botoesTeclado[4].dataset.num1 = "9";
// Aqui é meio estranho "9 ou 10", mas vamos manter igual ao HTML
// Observação: "10" é dois caracteres, mas tudo bem, tratamos como string
botoesTeclado[4].dataset.num2 = "10";

// ======== 2) Array para armazenar o índice dos botões clicados ========
let botoesPressionados = [];

// ======== 3) Referências aos campos de input ========
const senhaInput = document.getElementById('senha');            // mostra asteriscos
const indicesInput = document.getElementById('indicesClicados'); // envia índices ao servidor
const form = document.querySelector('form');

// ======== 4) Lógica de clique ========
botoesTeclado.forEach((botao, index) => {
  botao.addEventListener('click', () => {
    // Se for o último botão (backspace, índice 5), apaga o último
    if (index === 5) {
      botoesPressionados.pop();
      senhaInput.value = senhaInput.value.slice(0, -1);
      return;
    }

    // Caso contrário, se ainda não temos 6 cliques, adiciona o índice
    if (botoesPressionados.length < 6) {
      botoesPressionados.push(index);
      senhaInput.value += "*"; // só exibe asterisco
    }
  });
});

// ======== 5) Ao submeter o form, gravamos a lista de índices em indicesClicados ========
form.addEventListener('submit', (event) => {
  if (botoesPressionados.length !== 6) {
    event.preventDefault();
    alert("Senha incompleta! Você precisa clicar 6 vezes nos botões.");
    return;
  }

  // Exemplo: se botoesPressionados = [0,2,4,0,2,1], transformamos em JSON " [0,2,4,0,2,1] "
  indicesInput.value = JSON.stringify(botoesPressionados);

  // Submete o form normalmente depois disso.
});
