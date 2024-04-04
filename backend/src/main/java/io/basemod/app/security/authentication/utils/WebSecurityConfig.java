package io.basemod.app.security.authentication.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.Arrays.asList;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Value("${spring.websecurity.debug:true}")
    boolean webSecurityDebug;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.debug(webSecurityDebug);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        //.requestMatchers(HttpMethod.GET,"/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/artifacts/me/review").hasAnyAuthority("reviewer", "modeller", "admin")
                        .requestMatchers(HttpMethod.GET,"/api/artifacts/review").hasAnyAuthority("reviewer", "admin")
                        .requestMatchers(HttpMethod.GET,
                                "/api/biometricCharacteristics",
                                "/api/deploymentLocations",
                                "/api/resourceToProtects",
                                "/api/biometricAuthenticationSystemAndEvaluations",
                                "/api/deviceCategories",
                                "/api/sensors",
                                "/api/sensorDimensions",
                                "/api/dataSets",
                                "/api/samplingContexts",
                                "/api/sampleDevices",
                                "/api/features",
                                "/api/biometricSystemProcessingSteps",
                                "/api/experimentSpecificEvaluationCriterias",
                                "/api/implementationSpecificEvaluationCriterias",
                                "/api/resultMetrics",
                                "/api/baseEvaluationExtension").hasAnyAuthority("reviewer", "admin")
                        //.requestMatchers(HttpMethod.DELETE,"/api/**").permitAll()
                        //.requestMatchers(HttpMethod.GET,"/api/**").permitAll()
                        //.requestMatchers(HttpMethod.POST,"/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/**").hasAnyAuthority("reviewer", "modeller", "admin")
                        //.requestMatchers(HttpMethod.GET,"/auth/user").permitAll()
                        //.requestMatchers(HttpMethod.GET,"/").permitAll()
                        //.requestMatchers(HttpMethod.GET,"/index.html").permitAll()
                        .anyRequest().permitAll()
                ).csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationJwtTokenFilter(), RememberMeAuthenticationFilter.class);
        http.logout().logoutUrl("/auth/logout").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));

        return http.build();
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }


}
