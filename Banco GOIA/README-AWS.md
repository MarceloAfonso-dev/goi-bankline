# Banco GOI - Configuração Dinâmica para AWS

Este projeto foi configurado para funcionar dinamicamente tanto em ambiente local quanto no ambiente AWS (EC2, CloudFront, Route53).

## 🚀 Funcionalidades Implementadas

### 1. Sistema de Configuração Dinâmica
- **EnvironmentConfig.java**: Classe utilitária que detecta automaticamente o ambiente (local/AWS)
- **ConfigServlet.java**: Endpoint `/config` que fornece configurações para o frontend
- **DynamicConfigFilter.java**: Filtro que adiciona headers de configuração em todas as requisições
- **app-config.js**: JavaScript utilitário que gerencia endpoints dinamicamente

### 2. Detecção Automática de Ambiente
O sistema detecta automaticamente se está rodando em:
- **Local**: `localhost:8080`
- **EC2**: IP público da instância
- **CloudFront**: Domínio do CloudFront
- **Route53**: Domínio personalizado

## 🛠️ Configuração AWS

### Variáveis de Ambiente
Configure as seguintes variáveis de ambiente na instância EC2:

```bash
# Ambiente
export APP_ENVIRONMENT=aws

# Domínios (configure conforme sua infraestrutura)
export ROUTE53_DOMAIN=seudominio.com
export CLOUDFRONT_DOMAIN=d123456789.cloudfront.net
export EC2_PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)

# Contexto da aplicação
export CONTEXT_PATH=/BancoGOI
```

### 1. Configuração EC2

```bash
# 1. Instale Java 8+ e Tomcat
sudo yum update -y
sudo yum install java-1.8.0-openjdk tomcat -y

# 2. Configure as variáveis de ambiente
echo 'export APP_ENVIRONMENT=aws' >> ~/.bashrc
echo 'export EC2_PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)' >> ~/.bashrc

# 3. Configure o Tomcat para usar as variáveis
sudo nano /etc/tomcat/tomcat.conf
# Adicione: JAVA_OPTS="-Dapp.environment=aws"

# 4. Faça o deploy do WAR
sudo cp BancoGOI.war /var/lib/tomcat/webapps/
sudo systemctl start tomcat
sudo systemctl enable tomcat
```

### 2. Configuração CloudFront

```json
{
  "Origins": [{
    "DomainName": "seu-ec2-ip.compute.amazonaws.com",
    "OriginPath": "/BancoGOI",
    "CustomOriginConfig": {
      "HTTPPort": 8080,
      "OriginProtocolPolicy": "http-only"
    }
  }],
  "DefaultCacheBehavior": {
    "TargetOriginId": "ec2-origin",
    "ViewerProtocolPolicy": "redirect-to-https",
    "CachePolicyId": "managed-caching-disabled"
  }
}
```

### 3. Configuração Route53

```json
{
  "Type": "A",
  "Name": "seudominio.com",
  "AliasTarget": {
    "DNSName": "d123456789.cloudfront.net",
    "EvaluateTargetHealth": false,
    "HostedZoneId": "Z2FDTNDATAQYW2"
  }
}
```

## 🔧 Como Funciona

### 1. Detecção de Ambiente
O sistema usa múltiplas estratégias para detectar o ambiente:

```javascript
// Frontend (app-config.js)
const isAws = currentHost.includes('.amazonaws.com') || 
             currentHost.includes('.cloudfront.net') ||
             this.isEC2PublicIP(currentHost) ||
             currentHost !== 'localhost';
```

```java
// Backend (EnvironmentConfig.java)
public boolean isAwsEnvironment() {
    return "aws".equalsIgnoreCase(getEnvironment()) || 
           System.getenv("AWS_EXECUTION_ENV") != null ||
           System.getenv("EC2_INSTANCE_ID") != null;
}
```

### 2. Configuração Dinâmica de URLs
Todos os endpoints são construídos dinamicamente:

```javascript
// Exemplo de uso nos arquivos JS
await window.appConfig.loadConfig();
window.appConfig.fetch('login?acao=getPares')  // Funciona em qualquer ambiente
```

### 3. Fallback Automático
Se o endpoint `/config` não estiver disponível, o sistema usa detecção baseada na URL atual.

## 📁 Estrutura dos Arquivos Modificados

```
src/main/
├── java/br/com/goibankline/
│   ├── util/
│   │   └── EnvironmentConfig.java        # Configuração de ambiente
│   ├── filter/
│   │   └── DynamicConfigFilter.java      # Filtro para headers dinâmicos
│   └── servlet/
│       └── ConfigServlet.java            # Endpoint de configuração
├── resources/
│   └── app.properties                    # Propriedades da aplicação
└── webapp/
    ├── static/js/
    │   ├── app-config.js                 # Utilitário JS para config dinâmica
    │   ├── login.js                      # Atualizado para usar config dinâmica
    │   ├── home.js                       # Atualizado para usar config dinâmica
    │   ├── transferencia.js              # Atualizado para usar config dinâmica
    │   ├── extrato.js                    # Atualizado para usar config dinâmica
    │   └── cadastro.js                   # Atualizado para usar config dinâmica
    ├── templates/
    │   ├── login.html                    # Inclui app-config.js
    │   ├── home.html                     # Inclui app-config.js
    │   ├── transferencia.html            # Inclui app-config.js
    │   ├── extrato.html                  # Inclui app-config.js
    │   └── cadastro.html                 # Inclui app-config.js
    └── WEB-INF/
        └── web.xml                       # Configurações de servlets e filtros
```

## 🧪 Testes

### Teste Local
```bash
mvn clean package
mvn tomcat7:run
# Acesse: http://localhost:8080/BancoGOI
```

### Teste AWS
```bash
# Verifique as configurações
curl http://seu-dominio.com/BancoGOI/config

# Resposta esperada:
{
  "baseUrl": "https://seu-dominio.com",
  "apiBaseUrl": "https://seu-dominio.com/BancoGOI",
  "contextPath": "/BancoGOI",
  "environment": "aws",
  "isAws": true,
  "isLocal": false
}
```

## 🔍 Debugging

### Logs do Sistema
```java
// EnvironmentConfig.java registra automaticamente
System.out.println("Ambiente detectado: " + getEnvironment());
System.out.println("Base URL: " + getBaseUrl());
```

### Console do Browser
```javascript
// app-config.js registra automaticamente
console.log('Configurações da aplicação carregadas:', window.appConfig.getConfig());
```

## 🎯 Benefícios

1. **Zero Configuração Manual**: Detecta automaticamente o ambiente
2. **Compatibilidade Total**: Funciona local e AWS sem alterações
3. **Escalabilidade**: Suporta múltiplos domínios e CDNs
4. **Manutenibilidade**: Centraliza toda lógica de configuração
5. **Robustez**: Sistema de fallback em caso de falhas

## 📝 Notas Importantes

- O banco de dados já está configurado para AWS RDS
- Todos os caminhos de recursos são absolutos
- CORS está configurado automaticamente para ambiente AWS
- O sistema é compatível com HTTP e HTTPS
- Suporta redirecionamentos automáticos do CloudFront
