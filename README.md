building an e-commerce plaform with java and spring.

# Roadmap:
- [x] set up project
- [x] set up authentication and AUTHORIZATION
  - [x] username/password
  - [x] phone-number/otp with twilio
  - [x] auth0 login
  - [x] jwt token
  - [ ] api-key/api-secret
  - [ ] passkeys
- [x] implement inventory and claim mechanism to prevent overselling
- [x] add payment service
  - [x] simple api to simulate and mark payment completion
  - [ ] payment gateway(razorpay or stripe) integration
- [x] add async processing for payment and order status update using localstack sqs for local development
- [x] use integer for all ID column
- [x] add database migration tools using flyway
- [x] add static code analysis using sonarqube
- [x] set up docker-compose and local dev environment(hot loading) with linux inotify-tools and spring-boot-devtools
- [x] api documentation using swagger spring package
- [ ] add role based login for users separating merchants and customers
- [ ] design doc
- [ ] improve test coverage
- [ ] integrate github CI/CD
- [ ] standardise exception/error handling
- [ ] add validations for all data
- [ ] add paginations
- [ ] add configuration management using spring-config-server

# run all services, databases and sqs
```
docker-compose --env-file .env up
```

# API documentation pages(swagger-ui)
1. http://localhost:8080/api/docs/auth/swagger-ui/index.html
2. http://localhost:8080/api/docs/inventories/swagger-ui/index.html
3. http://localhost:8080/api/docs/orders/swagger-ui/index.html
4. http://localhost:8080/api/docs/payments/swagger-ui/index.html
5. http://localhost:8080/api/docs/products/swagger-ui/index.html
