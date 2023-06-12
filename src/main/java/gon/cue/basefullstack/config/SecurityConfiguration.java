package gon.cue.basefullstack.config;

import gon.cue.basefullstack.config.filter.SpaWebFilter;
import gon.cue.basefullstack.security.AuthoritiesConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private static String contentSecurityPolicy = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**").disable())
                .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
                .headers(headers ->
                        headers
                                .contentSecurityPolicy(csp -> csp.policyDirectives(contentSecurityPolicy))
                                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                .permissionsPolicy(permissions ->
                                        permissions.policy(
                                                "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                                        )
                                )
                )
                .authorizeHttpRequests(authz ->
                        // prettier-ignore
                        authz
                                .requestMatchers("/", "/index.html", "/*.js", "/*.map", "/*.css").permitAll()
                                .requestMatchers("/assets/**").permitAll()
                                .requestMatchers("/*.ttf","*.woff","*.woff2").permitAll()
                                .requestMatchers("/*.ico", "/*.png", "/*.svg", "/*.webapp").permitAll()
                                .requestMatchers("/app/**").permitAll()
                                .requestMatchers("/i18n/**").permitAll()
                                .requestMatchers("/content/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/authenticate").permitAll()
                                .requestMatchers("/api/register").permitAll()
                                .requestMatchers("/api/activate").permitAll()
                                .requestMatchers("/api/account/reset-password/init").permitAll()
                                .requestMatchers("/api/account/reset-password/finish").permitAll()
                                .requestMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/api/**").authenticated()
                                .requestMatchers("/v3/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                                .requestMatchers("/management/health").permitAll()
                                .requestMatchers("/management/health/**").permitAll()
                                .requestMatchers("/management/info").permitAll()
                                .requestMatchers("/management/prometheus").permitAll()
                                .requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }
}
