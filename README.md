building an e-commerce plaform with java and spring.

# Roadmap:
- [x] set up project
- [x] set up authentication and authorization 
- [x] implement inventory and claim mechanism
- [x] add payment service
- [x] add async processing for payment and order status update
- [ ] use integer for all ID column
- [ ] add database migration tools
- [ ] add static code analysis
- [x] set up docker-compose and local dev environment
- [ ] api documentation
- [ ] design doc


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
