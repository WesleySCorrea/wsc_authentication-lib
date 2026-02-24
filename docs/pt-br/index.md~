# WSC Authentication Lib 🔐

Biblioteca leve para autenticação JWT stateless em aplicações Spring Boot.

Projetada para simplificar a geração, validação e integração de tokens JWT com Spring Security.

---

## ⚙️ Pré-requisitos

- Java 17+
- Spring Boot 3+
- Spring Security

---

## 🚀 O que é esta biblioteca?

A WSC Authentication Lib fornece:

- Geração de Access Token
- Geração de Refresh Token
- Validação automática de assinatura e expiração
- Integração com filtro do Spring Security
- Modelo totalmente stateless (sem sessão)

OBS: A biblioteca não gerencia usuários nem persistência.
A responsabilidade de armazenamento e recuperação de usuários é da aplicação.

---

## 🎯 Quando usar?

Use esta biblioteca se você:

- Está construindo uma API REST com Spring Boot
- Precisa de autenticação JWT
- Quer evitar implementar manualmente geração e validação de tokens
- Deseja manter sua aplicação stateless

---

## 📦 Instalação rápida

Adicione a dependência ao seu projeto e configure as propriedades no application.yml:

### Dependencia

```xml
<dependency>
    <groupId>com.github.WesleySCorrea</groupId>
    <artifactId>authentication-lib</artifactId>
    <version>1.0.1</version>
</dependency>
```

### application.yml

```yml
wsc:
  auth:
    jwt:
      secret: sua-chave-secreta-super-segura
      accessTokenExpiration: 900000
      refreshTokenExpiration: 604800000
```
#### 🔑 secret

Chave usada para assinar os tokens (HS256).

#### ⏳ accessTokenExpiration

Tempo de expiração do Access Token (em milissegundos).

#### 🔄 refreshTokenExpiration

Tempo de expiração do Refresh Token (em milissegundos).

---

## 📚 Documentação

- 👉 [Guia Inicial](guia-inicial.md)
- 📌 [API](api.md)
