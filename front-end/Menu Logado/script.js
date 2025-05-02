document.addEventListener('DOMContentLoaded', () => {
    const toggleVisibility = document.getElementById('toggle-visibility');
    const saldoElement = document.querySelector('.saldo');
    const limiteElement = document.querySelector('.limite-valor');
    const limiteCartaoElement = document.querySelector('.limite-cartao');
    const valorFaturaElement = document.querySelector('.valor-fatura');

    // Valores originais
    const saldoOriginal = saldoElement.textContent;
    const limiteOriginal = limiteElement.textContent;
    const limiteCartaoOriginal = limiteCartaoElement.textContent;
    const valorFaturaOriginal = valorFaturaElement.textContent;

    // URLs das imagens
    const visibilityOn = "../assets/img/visibility.png"; // Ícone de visibilidade ativada
    const visibilityOff = "../assets/img/visibility-off.png"; // Ícone de visibilidade desativada

    // Alterna entre valores e asteriscos
    toggleVisibility.addEventListener('click', () => {
        if (saldoElement.textContent === saldoOriginal) {
            // Oculta os valores
            saldoElement.textContent = '****';
            limiteElement.textContent = '****';
            limiteCartaoElement.textContent = '****';
            valorFaturaElement.textContent = '****';

            // Troca para o ícone de visibilidade desativada
            toggleVisibility.src = visibilityOff;
        } else {
            // Mostra os valores originais
            saldoElement.textContent = saldoOriginal;
            limiteElement.textContent = limiteOriginal;
            limiteCartaoElement.textContent = limiteCartaoOriginal;
            valorFaturaElement.textContent = valorFaturaOriginal;

            // Troca para o ícone de visibilidade ativada
            toggleVisibility.src = visibilityOn;
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    // Controle do card de saldo
    const toggleSaldo = document.getElementById('toggle-saldo');
    const containerSaldo = document.querySelector('.container-saldo');

    toggleSaldo.addEventListener('click', () => {
        // Verifica se o card está expandido
        if (containerSaldo.classList.contains('retracted')) {
            // Expande o card
            containerSaldo.classList.remove('retracted');
            toggleSaldo.classList.remove('rotated');
        } else {
            // Retrai o card
            containerSaldo.classList.add('retracted');
            toggleSaldo.classList.add('rotated');
        }
    });

    // Controle do card de cartões
    const toggleCartoes = document.getElementById('toggle-cartoes');
    const containerCartoes = document.querySelector('.container-cartoes');

    toggleCartoes.addEventListener('click', () => {
        // Verifica se o card está expandido
        if (containerCartoes.classList.contains('retracted')) {
            // Expande o card
            containerCartoes.classList.remove('retracted');
            toggleCartoes.classList.remove('rotated');
        } else {
            // Retrai o card
            containerCartoes.classList.add('retracted');
            toggleCartoes.classList.add('rotated');
        }
    });
});