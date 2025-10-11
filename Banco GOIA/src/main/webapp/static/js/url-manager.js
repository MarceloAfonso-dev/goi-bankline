// Configuração dinâmica de URLs para suportar CloudFront e EC2
class UrlManager {
    constructor() {
        this.baseUrl = this.detectBaseUrl();
    }

    detectBaseUrl() {
        // Sempre usar o host atual (seja CloudFront ou EC2)
        const protocol = window.location.protocol;
        const host = window.location.host; // inclui porta se houver
        return `${protocol}//${host}`;
    }

    // Para navegação entre páginas (mantém o domínio atual)
    buildPageUrl(path) {
        // Remove barra inicial se houver
        const cleanPath = path.startsWith('/') ? path.substring(1) : path;
        return `${this.baseUrl}/${cleanPath}`;
    }

    // Para chamadas de API (mantém o contexto atual)
    buildApiUrl(endpoint) {
        const cleanEndpoint = endpoint.startsWith('/') ? endpoint.substring(1) : endpoint;
        return `${this.baseUrl}/${cleanEndpoint}`;
    }

    // Navegação segura que preserva o domínio atual
    navigateTo(path) {
        window.location.href = this.buildPageUrl(path);
    }

    // Para redirect para home
    goHome() {
        this.navigateTo('templates/home.html');
    }

    // Para logout (volta ao index)
    logout() {
        this.navigateTo('index.html');
    }
}

// Instância global
window.urlManager = new UrlManager();