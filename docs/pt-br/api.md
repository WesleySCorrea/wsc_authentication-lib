# 📘 API Reference

Este documento descreve os principais serviços disponibilizados pela WSC Authentication Lib.

A biblioteca expõe três operações principais responsáveis pelo ciclo completo de autenticação baseado em JWT:

1. generateHashPassword
2. authenticate
3. authenticateWithRefreshToken


## 1️⃣ generateHashPassword

Responsável por gerar o hash seguro de uma senha utilizando o encoder configurado na aplicação.

### 📌 Responsabilidade

- Aplicar algoritmo de hash na senha
- Retornar senha criptografada pronta para persistência

### 🧠 Quando utilizar

Deve ser utilizado no momento de cadastro de usuário, antes de salvar a senha no banco de dados.

### 💻 Exemplo de uso

A aplicação consumidora deverá chamar o seguinte método para efetuar essa operação:

```java 
String hashedPassword = authService.generateHashPassword(rawPassword);
```

🔐 Importante

A biblioteca não salva a senha.

A persistência é responsabilidade da aplicação.

Nunca armazene senha em texto puro (plain text).

⚠️ Não utilize outro algoritmo de hash em conjunto com as funções da biblioteca,
pois isso pode comprometer a validação da senha durante o processo de autenticação.


## 2️⃣ authenticate

Responsável por autenticar um usuário e gerar os tokens JWT.

### 📌 Responsabilidade

- Validar a senha informada
- Gerar Access Token
- Gerar Refresh Token
- Retornar ambos para o cliente

### 🧠 Quando utilizar

Deve ser utilizado durante o processo de login,
quando for necessário validar a senha informada
com a senha previamente armazenada com hash.


### 💻 Exemplo de uso

A aplicação consumidora deverá chamar o seguinte método para efetuar essa operação:

```java 
AuthenticationResponse response = authService.authenticate(user, rawPassword);
```

### 📤 Retorno esperado

```json
{
    "accessToken": "exemplo de access token",
    "refreshToken": "exemplo de refresh token",
    "tokenType": "Bearer",
    "expiresIn": 111111,
    "user": {
        "id": 1,
        "name": "exemplo",
        "email": "exemplo@example.net",
        "role": "EXEMPLO"
    }
}
```

### ❌ Possíveis Erros

- Credenciais inválidas → 401 Unauthorized
- Usuário inexistente → 404 Not Found (definido pela aplicação)

### ⚠️ Observações

A busca do usuário no banco é responsabilidade da aplicação.

Caso a senha esteja incorreta, deve-se retornar erro de autenticação.

A biblioteca não gerencia sessões.


## 3️⃣ authenticateWithRefreshToken

Responsável por gerar um novo Access Token a partir de um Refresh Token válido.

### 📌 Responsabilidade

- Validar o refresh token
- Gerar novo access token e refresh token
- Retornar novo conjunto de token ao cliente

### 🧠 Quando utilizar

Quando o access token expirar, fazer autenticação com o refresh token.

### 💻 Exemplo de uso

A aplicação consumidora deverá chamar o seguinte método para efetuar essa operação:

```java 
TokenResponse response = authService.authenticateWithRefreshToken(refreshToken);
```

### 📤 Retorno esperado

```json
{
    "accessToken": "exemplo de access token",
    "refreshToken": "exemplo de refresh token",
    "tokenType": "Bearer",
    "expiresIn": 111111, 
    "user": {
        "id": 1,
        "name": "exemplo",
        "email": "exemplo@example.net",
        "role": "EXEMPLO"
    }
}
```

### ❌ Possíveis Erros

- Refresh token inválido ou expirado → 401 Unauthorized

## 🔐 Modelo Stateless

A biblioteca opera em modo stateless, ou seja:

- Nenhuma sessão é armazenada no servidor
- A autenticação não depende de estado compartilhado
- Toda requisição protegida deve conter um JWT válido
- A validação ocorre a cada requisição através do filtro configurado

Esse modelo melhora a escalabilidade e elimina dependência de armazenamento de sessão.

## 📊 Resumo das Funções

| Função	                      | Responsabilidade              |
|------------------------------|-------------------------------|
| generateHashPassword	        | Gerar hash seguro da senha    |
| authenticate                 | Login e geração de tokens     |
| authenticateWithRefreshToken | Renovação do access token     |