building a e-commerce plaform with java and spring.

# Roadmap:
[] set up project
[] set up authentication and authorization 
[] implement inventory and claim mechanism
[] add payment service
[] add async processing for payment and order status update
[] add migration tools
[] add migration tools


# queue:
docker run \
  --rm -it \
  -p 127.0.0.1:4566:4566 \
  -p 127.0.0.1:4510-4559:4510-4559 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  localstack/localstack
