# Keyclaok Integration with Spring Boot

It is a sample application which will demonstrate how to secure API using keyclaoak.
There are couple of way by which we can configure keyclaok to secure the endpoint.
In this example we will be doing following: 
- Download keyclaok[Keyclaok](https://www.keycloak.org/downloads) & run it locally. 
- Create a realm named "springboot". You can name it anything you like.
- Create a public client on keyclaok
- Manually Create an user and assign some roles to user.
- We will use postman to request access token and using client id and user credentials.
- Create a simple microservice
- Use access token to invoke that microservice
- Verify JWT token 
- Based on role in the token allow user to access the application. 

## Keyclaok Setup 
- Download [Keyclaok](https://www.keycloak.org/downloads). Mac user use /bin/kc.sh  & Windows user use /bin/kc.bat
- Visit http://localhost:8080/ , It will open adim page , setup admin user and credential and login to keyclaok. 
- Create realm 
![REALM_1](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/1_CREATE_REALM.png)
![REALM_2](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/2_CREATE_REALM_2.png)
- create client, For simplicity we have used public client.
![CLIENT_1](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/3_CLIENT_1.png)
![CLIENT_1](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/4_CREATE_CLIENT_2.png)
- Create a role of user clients roles menu 
![ROLE_1](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/5_CREATE_ROLE_1.png)
- Create user
![USER_1](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/6_CREATE_USER_1.png)
![USER_2](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/7_CREATE_USER_2.png)

- Assign role to the user 
![ASSIGN_ROLE](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/8_ASSIGN_ROLE_1.png)
- List of all endpoints are avilable on realm general setting page
- ![END_POINT](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/9_ENDPOINTS.png)
- Use Token endpoint to request token.
- ![POSTMAN_1](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/PC_1.png)
- ![POSTMAN_2](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/PC_2.png)
- Verify the roles in jwt token from jwt.io
- ![JWT](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/JWT.png)

As we can see role DEV_USER is assigned to the user. 
## Creating Restful API
- Go To start.spring.io. Create spingboot project. 
- Dependencies - Refer pom.xml
   - WEB (spring-boot-starter-web)
   - SECURITY (spring-boot-starter-security)
    - RESOURCE SERVER(spring-boot-starter-oauth2-resource-server)
- HelloWorldController.java provide a basic endpoint /hello which return a string "Hello World"
We will be allow the user with role DEV_USER to access this endpoint. 
- In order to allow user of given role to make api calls, we need to perform two tasks
   - Verify if JWT token signature- Keyclaok provide endpoint to  get the certificate to verify JWT. In application.properties we need to define the property
`spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/SpringBoot/protocol/openid-connect/certs`
    - Extract roles from token for Authorization.
        - As the token type is JWT, spring security use JwtGrantedAuthoritiesConverter to parse the token , and it looks for the Authority from claim name "scope, scp" 

       ![SC](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/SC_1.png)

         ![SC_1](https://raw.githubusercontent.com/prabhat0123/keycloak-springboot/main/images/SC_2.png)
        - Granted Authority which spring security after parsing the token will be SCOPE_PROFILE & SCOPE_EMAIL , which is incorrect. As spring security don't know which field to look for token , So we need to provide implementation of org.springframework.core.convert.converter.Converter Interface. 
```
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
```
It takes the client name and based on the client name it extract the roles from the token and return the granted authority object.

-  configure WebSecurityConfigurerAdapter to match the pattern and role.  
