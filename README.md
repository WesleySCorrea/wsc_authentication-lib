# WSC Authentication Lib 🔐

A lightweight, stateless JWT authentication library built for Spring Boot applications.

Designed to simplify token generation, validation, and integration with Spring Security.

## 📖 Documentation

The official documentation is available at:

https://wesleyscorrea.github.io/wsc_authentication-lib/

## 🚀 Core Features

### 1️⃣ JWT Token Generation
- Generates secure access and refresh tokens
- Custom claims support
- Configurable expiration time

### 2️⃣ Stateless Authentication
- Fully stateless security model
- Integrates with Spring Security filter chain
- No session usage

### 3️⃣ Automatic Token Validation
- Signature verification (HS256)
- Expiration validation
- Issuer validation

## 📌 Public API

The library exposes the following operations:

### 🔐 Password Hashing

```java
String generateHashPassword(String password);
```

### 🔑 Authentication

```java
AuthResponse authenticate(String rawPassword,String password,JwtUser user);
```

### 🔄 Refresh Authentication

```java
AuthResponse authenticateWithRefreshToken(String refreshToken);
```

All token parsing, validation, and claim extraction logic is handled internally.

## 📦 Installation

Add the dependency:

```xml
<dependency>
    <groupId>com.wsc.soft</groupId>
    <artifactId>authentication-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

Add the repository in the pom.xml:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

## 4️⃣ ⚙️ Configuration Example

application.yml:

```application.yml:
wsc:
  auth:
    jwt:
      secret: your-super-secret-key
      accessTokenExpiration: 900000
      refreshTokenExpiration: 604800000
```

## 📊 Feature Overview

| Feature                        | Supported |
|--------------------------------|-----------|
| Access Token Generation        | ✅         |
| Refresh Token Support          | ✅         |
| Stateless Authentication       | ✅         |
| Role-based Claims              | ✅         |
| Signature Validation           | ✅         |
| Custom Expiration Time         | ✅         |
| Spring Security Integration    | ✅         |


## 🔐 Security Notes

- Tokens are signed using HS256.
- Claims are validated before authentication.
- The library does not manage user storage.
- Authorization decisions should be handled by the application layer.


## 🏗 Design Principles

- Stateless by design
- Minimal dependencies
- Framework-independent token engine
- Spring Security integration layer
- Application-controlled authorization

## 📦 Requirements

This library requires the following dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 👤 Implementing JwtUser

Applications using this library must provide an implementation of `JwtUser`.

This interface defines the minimum user information required for token generation.
It allows the library to remain independent from Spring Security's `UserDetails`.

Example:

```java
public class UserClass implements JwtUser {
    private Long id;
    private String name;
    private String email;
    private String role;
}
```

## ⚙️ Security Configuration Example

Below is an example of how to integrate `JwtAuthenticationFilter`
into your Spring Boot application.

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
                    .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/refreshlogin").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

## ⚠️ Important  

The `JwtAuthenticationFilter` is responsible for:

 - Extracting the token from the Authorization header
 - Validating the JWT signature
 - Setting the authentication in the SecurityContext

If the filter is not registered, JWT authentication will not work.


## 🔐 Notes 

- The application must use `SessionCreationPolicy.STATELESS`
- CSRF must be disabled for REST APIs
- The JWT filter must be added before `UsernamePasswordAuthenticationFilter`
- The library does not manage users or persistence

### 🔄 Authentication Flow

1. Client sends request with `Authorization: Bearer <token>`
2. `JwtAuthenticationFilter` intercepts the request
3. Token is validated (signature + expiration)
4. Authentication is set in Spring Security context
5. Request proceeds if valid