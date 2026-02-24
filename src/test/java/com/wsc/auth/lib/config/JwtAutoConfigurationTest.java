package com.wsc.auth.lib.config;

import org.junit.jupiter.api.Test;
import com.wsc.auth.lib.service.JwtService;
import com.wsc.auth.lib.service.AuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class JwtAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(JwtAutoConfiguration.class)
                    .withPropertyValues(
                            "wsc.auth.jwt.secret=test-secret-key-12345678901234567890",
                            "wsc.auth.jwt.accessTokenExpiration=3600000",
                            "wsc.auth.jwt.refreshTokenExpiration=7200000"
                    );

    @Test
    void shouldLoadJwtServiceBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JwtService.class);
        });
    }

    @Test
    void shouldLoadAuthServiceBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AuthenticationService.class);
        });
    }

    @Test
    void shouldCreatePasswordEncoderIfMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PasswordEncoder.class);
        });
    }
}
