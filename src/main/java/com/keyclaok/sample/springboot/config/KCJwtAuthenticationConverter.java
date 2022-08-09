package com.keyclaok.sample.springboot.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class KCJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private final String resourceId;

	public KCJwtAuthenticationConverter(String resourceId) {
		this.resourceId = resourceId;
	}

	@SuppressWarnings("unchecked")
	private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt, final String resourceId) {
		Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
		Map<String, Object> resource;
		Collection<String> resourceRoles;
		if (resourceAccess != null && (resource = (Map<String, Object>) resourceAccess.get(resourceId)) != null
				&& (resourceRoles = (Collection<String>) resource.get("roles")) != null)
			return resourceRoles.stream().map(x -> new SimpleGrantedAuthority("ROLE_" + x)).collect(Collectors.toSet());
		return Collections.emptySet();
	}

	@Override
	public AbstractAuthenticationToken convert(final Jwt source) {
		return new JwtAuthenticationToken(source, extractResourceRoles(source, resourceId));
	}

}