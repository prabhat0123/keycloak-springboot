package com.keyclaok.sample.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.antMatchers("/api/hello")
		.hasAuthority("DEV_USER")
				.anyRequest()
				.authenticated().and().oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new KCJwtAuthenticationConverter("account"));
				
	}
}
