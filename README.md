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
- [x] set up docker-compose and local dev environment
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

# queue:
command to run amazon sqs compatible queue
```
docker run \
  --rm -it \
  -p 127.0.0.1:4566:4566 \
  -p 127.0.0.1:4510-4559:4510-4559 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  localstack/localstack
```

# sonarqube static code analysis command
```
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=ecom \
  -Dsonar.projectName='ecom' \
  -Dsonar.host.url=http://sonarqube:9000 \
  -Dsonar.token=sqp_1512b34fa0a47d8dfbd1741d458d3b204e8fd6aa;
```