        return this.config;
    }

    /**
     * Detecta o ambiente baseado na URL atual
     */
    detectEnvironment() {
        const currentHost = window.location.hostname;
        const currentPort = window.location.port;
        const currentProtocol = window.location.protocol;

        let baseUrl = `${currentProtocol}//${currentHost}`;
        if (currentPort && currentPort !== '80' && currentPort !== '443') {
            baseUrl += `:${currentPort}`;
        }

        // Detecta se está em ambiente AWS
        const isAws = currentHost.includes('.amazonaws.com') ||
                     currentHost.includes('.cloudfront.net') ||
                     this.isEC2PublicIP(currentHost) ||
                     currentHost !== 'localhost';

        return {
            baseUrl: baseUrl,
            apiBaseUrl: baseUrl + '/BancoGOI',
            contextPath: '/BancoGOI',
            environment: isAws ? 'aws' : 'local',
            isAws: isAws,
            isLocal: !isAws
        };
    }

    /**
     * Verifica se o hostname parece ser um IP público do EC2
     */
    isEC2PublicIP(hostname) {
        const ipPattern = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
        return ipPattern.test(hostname);
    }

    /**
     * Constrói uma URL de endpoint de forma dinâmica
     */
    buildEndpointUrl(endpoint) {
        if (!this.config) {
            console.error('Configurações não carregadas. Chame loadConfig() primeiro.');
            return endpoint;
        }

        // Remove barras no início do endpoint se existirem
        const cleanEndpoint = endpoint.startsWith('/') ? endpoint.substring(1) : endpoint;

        // Se já é uma URL completa, retorna como está
        if (endpoint.startsWith('http')) {
            return endpoint;
        }

        // Para endpoints relativos, usa a configuração atual
        const currentPath = window.location.pathname;
        if (currentPath.includes('/BancoGOI')) {
            // Já está no contexto da aplicação
            return `/${cleanEndpoint}`;
        } else {
            // Precisa adicionar o contexto
            return `${this.config.apiBaseUrl}/${cleanEndpoint}`;
        }
    }

    /**
     * Faz uma requisição fetch com URL dinâmica
     */
    async fetch(endpoint, options = {}) {
        if (!this.loaded) {
            await this.loadConfig();
        }

        const url = this.buildEndpointUrl(endpoint);

        // Adiciona headers padrão se não especificados
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        };

        const mergedOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...(options.headers || {})
            }
        };

        return fetch(url, mergedOptions);
    }

    /**
     * Obtém a configuração atual
     */
    getConfig() {
        return this.config;
    }

    /**
     * Verifica se está em ambiente AWS
     */
    isAwsEnvironment() {
        return this.config ? this.config.isAws : false;
    }

    /**
     * Verifica se está em ambiente local
     */
    isLocalEnvironment() {
        return this.config ? this.config.isLocal : true;
    }
}

// Instância global
window.appConfig = new AppConfig();

// Inicializa automaticamente quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', async () => {
    try {
        await window.appConfig.loadConfig();
        console.log('Configurações da aplicação carregadas:', window.appConfig.getConfig());
    } catch (error) {
        console.error('Erro ao carregar configurações:', error);
    }
});
/**
 * Utilitário para gerenciar configurações dinâmicas da aplicação
 * Funciona tanto localmente quanto no ambiente AWS (EC2, CloudFront, Route53)
 */
class AppConfig {
    constructor() {
        this.config = null;
        this.loaded = false;
    }

    /**
     * Carrega as configurações do servidor de forma assíncrona
     */
    async loadConfig() {
        if (this.loaded) return this.config;

        try {
            // Tenta carregar do endpoint /config
            const response = await fetch('/config');
            if (response.ok) {
                this.config = await response.json();
                this.loaded = true;
                return this.config;
            }
        } catch (error) {
            console.warn('Não foi possível carregar configurações do servidor, usando fallback');
        }

        // Fallback: detecta ambiente baseado na URL atual
        this.config = this.detectEnvironment();
        this.loaded = true;
