building an e-commerce plaform with java and spring.

# Roadmap:
- [x] set up project
- [x] set up authentication and AUTHORIZATION 
- [x] implement inventory and claim mechanism
- [x] add payment service
- [x] add async processing for payment and order status update
- [x] use integer for all ID column
- [x] add database migration tools
- [x] add static code analysis
- [x] set up docker-compose and local dev environment(hot loading)
- [x] api documentation
- [ ] add role based login for users for merchant and customers
- [ ] design doc
- [ ] improve test coverage
- [ ] integrate github CI/CD
- [ ] standardise exception/error handling
- [ ] add validations for all data
- [ ] refactor dto and move all dto conversion from service to controller
- [ ] add paginations
- [ ] add configuration management(build for different environment)

# run all services
```
docker-compose --env-file .env up
```

# API documentation pages(swagger-ui)
1. http://localhost:8080/swagger-ui/index.html
2. http://localhost:8080/api/docs/inventories/swagger-ui/index.html
3. http://localhost:8080/api/docs/orders/swagger-ui/index.html
4. http://localhost:8080/api/docs/payments/swagger-ui/index.html
5. http://localhost:8080/api/docs/products/swagger-ui/index.html
