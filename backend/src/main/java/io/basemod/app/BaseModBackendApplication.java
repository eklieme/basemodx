package io.basemod.app;

import io.basemod.app.security.authentication.utils.EntityCreatorAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static java.util.Arrays.asList;

@EnableMongoAuditing
@SpringBootApplication
public class BaseModBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseModBackendApplication.class, args);
	}


	@Bean
	public FilterRegistrationBean simpleCorsFilter() {

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.setAllowedOrigins(asList("http://localhost:8080"));
		config.setAllowedHeaders(asList("Access-Control-Allow-Headers",
				"Access-Control-Allow-Origin",
				"Access-Control-Allow-Credentials",
				"Content-Type",
				"X-Requested-With",
				"Authorization"));
		config.setAllowedMethods(asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));

		source.registerCorsConfiguration("/api/**", config);
		source.registerCorsConfiguration("/actuator/**", config);
		source.registerCorsConfiguration("/auth/**", config);
		source.registerCorsConfiguration("/login", config);
		FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

		return bean;
	}

	@Bean
	public EntityCreatorAwareImpl<String> myAuditorProvider() {
		return new EntityCreatorAwareImpl();
	}

}