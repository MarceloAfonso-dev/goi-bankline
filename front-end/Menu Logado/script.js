document.addEventListener('DOMContentLoaded', () => {
    const toggleVisibility = document.getElementById('toggle-visibility');
    const saldoElement = document.querySelector('.saldo');
    const limiteElement = document.querySelector('.limite-valor');

    // Valores originais
    const saldoOriginal = saldoElement.textContent;
    const limiteOriginal = limiteElement.textContent;

    // Alterna entre valores e asteriscos
    toggleVisibility.addEventListener('click', () => {
        if (saldoElement.textContent === saldoOriginal) {
            // Oculta os valores
            saldoElement.textContent = '****';
            limiteElement.textContent = '****';
            saldoElement.classList.add('hidden-value');
            limiteElement.classList.add('hidden-value');
        } else {
            // Mostra os valores originais
            saldoElement.textContent = saldoOriginal;
            limiteElement.textContent = limiteOriginal;
            saldoElement.classList.remove('hidden-value');
            limiteElement.classList.remove('hidden-value');
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    const toggleCard = document.getElementById('toggle-card');
    const containerSaldo = document.querySelector('.container-saldo');

    // Define o estado inicial como expandido
    containerSaldo.classList.add('retracted');

    toggleCard.addEventListener('click', () => {
        // Verifica se o card está expandido
        if (containerSaldo.classList.contains('retracted')) {
            // Retrai o card
            containerSaldo.classList.remove('retracted');
            toggleCard.classList.remove('rotated');
        } else {
            // Expande o card
            containerSaldo.classList.add('retracted');
            toggleCard.classList.add('rotated');
        }
    });
});