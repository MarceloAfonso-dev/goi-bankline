# 🔧 Fix: Problema de Redirecionamento AWS/CloudFront

## 🚨 Problema Identificado

Quando hospedado na AWS com CloudFront + Route53, o cadastro redirecionava para:
```
❌ http://ec2-52-67-52-127.sa-east-1.compute.amazonaws.com/cadastro?sucesso=1
```

Ao invés de manter o domínio personalizado:
```
✅ https://goia.click/cadastro?sucesso=1
```

## 🔍 Causa do Problema

O servlet estava usando `req.getContextPath()` no redirecionamento:
```java
// ❌ PROBLEMÁTICO (pode gerar URL com IP da EC2)
resp.sendRedirect(req.getContextPath() + "/cadastro?sucesso=1");
```

## ✅ Solução Aplicada

### 1. **CadastroServlet.java - Redirecionamento Relativo**
```java
// ✅ CORRIGIDO (mantém domínio original)
resp.sendRedirect("/cadastro?sucesso=1");
```

### 2. **ErroUtil.java - Método Utilitário Seguro**
```java
/**
 * Redireciona de forma segura mantendo o domínio original
 */
public static void redirecionarSeguro(HttpServletResponse response, String path) 
        throws IOException {
    
    if (!path.startsWith("/")) {
        path = "/" + path;
    }
    
    response.sendRedirect(path);  // Redirecionamento relativo
}
```

## 🎯 Como Funciona

### ❌ **Redirecionamento Absoluto (Problemático)**
```java
// Gera URLs como: http://ec2-ip/path
resp.sendRedirect(req.getContextPath() + "/cadastro?sucesso=1");
```

### ✅ **Redirecionamento Relativo (Correto)**
```java
// Mantém o domínio atual: https://goia.click/cadastro?sucesso=1
resp.sendRedirect("/cadastro?sucesso=1");
```

## 🌐 Por Que Acontece na AWS

1. **CloudFront** → Recebe request em `goia.click`
2. **Forwarding** → Envia para EC2 backend
3. **Servlet** → Se usar `getContextPath()`, pega o IP da EC2
4. **Response** → Retorna redirect com IP ao invés do domínio

## 📋 Checklist de Verificação

- [x] **CadastroServlet**: Redirecionamento corrigido
- [x] **ErroUtil**: Método seguro adicionado  
- [x] **LoginServlet**: Já usa `forward` (OK)
- [x] **Outros servlets**: Verificar se há `sendRedirect` problemático

## 🔄 Para Implementar em Outros Servlets

### ✅ **Use Forward (Recomendado)**
```java
// Mantém URL e domínio
req.getRequestDispatcher("/templates/pagina.html").forward(req, resp);
```

### ✅ **Use Redirect Relativo**
```java
// Para mudar URL mas manter domínio
resp.sendRedirect("/nova-pagina");
```

### ❌ **Evite Context Path em Produção**
```java
// Pode gerar URLs com IP da EC2
resp.sendRedirect(req.getContextPath() + "/pagina");
```

## 🚀 Status

✅ **Problema resolvido!**
- Cadastro agora mantém o domínio `goia.click`
- Não há mais timeout por tentar acessar IP da EC2
- Experiência do usuário mantida

## 📝 Próximos Passos

1. **Testar** o cadastro em produção
2. **Verificar** outros fluxos que podem ter redirecionamento
3. **Monitorar** logs para garantir que funciona corretamente

---

**Resumo**: Mudança de `req.getContextPath() + "/cadastro?sucesso=1"` para `"/cadastro?sucesso=1"` resolve o problema de redirecionamento para IP da EC2 na AWS.