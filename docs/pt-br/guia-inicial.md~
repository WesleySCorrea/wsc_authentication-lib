# 🚀 Guia Inicial

Este guia mostra passo a passo como integrar a WSC Authentication Lib
em uma aplicação Spring Boot. 

O objetivo desta biblioteca é facilitar a implementação de autenticação JWT
em aplicações Spring Boot de forma simples e stateless.

---

## ✅ Pré-requisitos

- Java 21+
- Spring Boot
- spring-boot-starter-security
- Projeto configurado como API REST

## 📌 Utilizando a Biblioteca

### 1️⃣ Adicionar a Dependência

No seu `pom.xml` adicione a dependência da biblioteca:

```xml
<dependency>
    <groupId>com.github.WesleySCorrea</groupId>
    <artifactId>authentication-lib</artifactId>
    <version>1.0.1</version>
</dependency>
```

Adicione também o repositorio do github:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### 2️⃣ Configurar application.yml

Configure seu `application.yml` de acordo com suas preferências:

```yml
wsc:
  auth:
    jwt:
      secret: sua-chave-secreta-super-segura
      accessTokenExpiration: 900000
      refreshTokenExpiration: 604800000
```

### 3️⃣ Implementar JwtUser

Para o funcionamento correto da biblioteca, a aplicação consumidora deve
fornecer uma classe que represente o usuário do sistema e implemente
a interface `JwtUser`.

Essa interface define as informações mínimas necessárias para geração
dos tokens JWT.

A biblioteca não possui acesso ao banco de dados e não gerencia
usuários ou persistência.

⚠️ A biblioteca não exige `UserDetails`.

```java
public class ApplicationUser implements JwtUser {

    private Long id;
    private String name;
    private String email;
    private String role;

    ...
}
```

### 4️⃣ Registrar o JwtAuthenticationFilter

Como a biblioteca integra-se ao Spring Security, é necessário
configurar o `SecurityFilterChain` na aplicação consumidora.

Segue o exemplo de configuração compatível para utilização da biblioteca:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST,"/users/register").permitAll()
                    .requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST,"/auth/refreshlogin").permitAll()
                    .anyRequest().authenticated()
            ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

Essa configuração define quais endpoints estarão públicos e quais
exigirão autenticação via JWT.

A classe `JwtAuthenticationFilter` é responsável por:

- Extrair o token do header `Authorization`
- Validar assinatura e expiração
- Popular o `SecurityContext`
- Permitir ou bloquear a requisição

Sem o registro do filtro, a validação automática do JWT não ocorrerá.

```java
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

### 5️⃣ Aplicação pronta para utilização

Após concluir os passos anteriores:

- A geração de tokens estará disponível através do método `authenticate`
- O refresh token poderá ser utilizado para gerar novos access tokens
- Todas as requisições protegidas serão automaticamente validadas pelo filtro JWT
- A aplicação estará operando em modo stateless

Agora sua API está preparada para autenticação segura baseada em JWT.

## 📌 Resumo da Integração

✔ Dependência adicionada  
✔ Propriedades configuradas  
✔ JwtUser implementado  
✔ Filtro registrado  
✔ Aplicação operando com autenticação JWT

## 🔐 Segurança

- Nunca versionar o secret no repositório.
- Utilize variáveis de ambiente em produção.
- Recomenda-se chave com no mínimo 256 bits.
- Utilize HTTPS obrigatoriamente em produção.

### ℹ️ Para detalhes sobre os métodos disponíveis (authenticate, refresh, etc),
Consulte a seção [API](api.md).