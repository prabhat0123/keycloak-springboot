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
- Download keyclaok[Keyclaok](https://www.keycloak.org/downloads). Mac user use /bin/kc.sh  & Windows user use /bin/kc.bat
- Visit http://localhost:8080/ , It will open adim page , setup admin user and credential and login to keyclaok. 
- Create realm 
- create client, For simplicity we have used public client.
- Create a role in client
- Create user
- Assign role to the user 
- Get access token for the user. List of all endpoints are avilable on realm general setting page. - Verify the roles in jwt token from jwt.io
