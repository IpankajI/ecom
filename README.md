building a e-commerce plaform with java and spring.

# Roadmap:
- [x] set up project
- [x] set up authentication and authorization 
- [x] implement inventory and claim mechanism
- [x] add payment service
- [x] add async processing for payment and order status update
- [ ] add database migration tools
- [ ] add static code analysis
- [ ] set up docker-compose and local dev environment


# queue:
docker run \
  --rm -it \
  -p 127.0.0.1:4566:4566 \
  -p 127.0.0.1:4510-4559:4510-4559 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  localstack/localstack
