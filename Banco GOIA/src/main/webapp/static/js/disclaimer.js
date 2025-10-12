/* ═══════════════════════════════════════════════════════════════════
   DISCLAIMER ACADÊMICO JS - Sistema de Aviso do Projeto
   ═══════════════════════════════════════════════════════════════════ */

(function() {
    'use strict';
    
    // Constante para chave do localStorage
    const DISCLAIMER_KEY = 'goia-disclaimer-closed';
    
    // Função para verificar se o disclaimer já foi fechado
    function isDisclaimerClosed() {
        return localStorage.getItem(DISCLAIMER_KEY) === 'true';
    }
    
    // Função para marcar disclaimer como fechado
    function setDisclaimerClosed() {
        localStorage.setItem(DISCLAIMER_KEY, 'true');
    }
    
    // Função para criar o HTML do disclaimer
    function createDisclaimerHTML() {
        return `
            <div class="disclaimer-academico" id="disclaimerAcademico">
                <div class="disclaimer-content">
                    <span class="disclaimer-icon">⚠️</span>
                    <p class="disclaimer-text">
                        <strong>Projeto acadêmico:</strong> Por favor, não inclua informações sensíveis ou pessoais reais.
                    </p>
                </div>
                <button class="disclaimer-close" id="disclaimerClose" title="Fechar aviso" aria-label="Fechar aviso acadêmico">
                    ✕
                </button>
            </div>
        `;
    }
    
    // Função para adicionar o disclaimer ao body
    function addDisclaimer() {
        if (isDisclaimerClosed()) {
            return; // Não adiciona se já foi fechado
        }
        
        // Cria o container do disclaimer
        const disclaimerContainer = document.createElement('div');
        disclaimerContainer.innerHTML = createDisclaimerHTML();
        
        // Adiciona ao final do body
        document.body.appendChild(disclaimerContainer.firstElementChild);
        
        // Adiciona classe ao body para ajuste de padding
        document.body.classList.add('disclaimer-visible');
        
        // Configura evento de clique no botão fechar
        setupCloseButton();
        
        console.log('📢 Disclaimer acadêmico exibido');
    }
    
    // Função para configurar o botão de fechar
    function setupCloseButton() {
        const closeButton = document.getElementById('disclaimerClose');
        const disclaimerElement = document.getElementById('disclaimerAcademico');
        
        if (closeButton && disclaimerElement) {
            closeButton.addEventListener('click', function() {
                closeDisclaimer(disclaimerElement);
            });
        }
    }
    
    // Função para fechar o disclaimer com animação
    function closeDisclaimer(disclaimerElement) {
        // Adiciona classe para animação de saída
        disclaimerElement.classList.add('hidden');
        
        // Remove classe do body
        document.body.classList.remove('disclaimer-visible');
        
        // Remove elemento após animação
        setTimeout(() => {
            if (disclaimerElement.parentNode) {
                disclaimerElement.parentNode.removeChild(disclaimerElement);
            }
        }, 300); // Tempo da transição CSS
        
        // Salva no localStorage que foi fechado
        setDisclaimerClosed();
        
        console.log('✅ Disclaimer acadêmico fechado');
    }
    
    // Função para resetar disclaimer (útil para desenvolvimento)
    function resetDisclaimer() {
        localStorage.removeItem(DISCLAIMER_KEY);
        console.log('🔄 Disclaimer resetado - será exibido novamente');
    }
    
    // Inicialização quando DOM estiver pronto
    function init() {
        // Verifica se já tem disclaimer na página para evitar duplicatas
        if (document.getElementById('disclaimerAcademico')) {
            return;
        }
        
        addDisclaimer();
    }
    
    // Executa quando DOM estiver carregado
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
    
    // Expõe função global para resetar (apenas para debugging)
    window.resetDisclaimerAcademico = resetDisclaimer;
    
})();