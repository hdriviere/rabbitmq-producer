# RabbitMQ Producer

# Prerequisites
  - Docker
  - Docker-compose
 
# Configuration
  
  First, allow the `setup.sh` script to run and run it.
  
  `chmod u+x setup.sh && ./script.sh`

The `setup.sh` script is going to:
1. Download the IMDB file inside `/tmp`
2. Unzip it
3. Build the docker image of the project
4. And finally run the RabbitMQ instance (inside a container)
 
##### Then you just have to run:
  
- `docker-compose up producer` in order to send data to RabbitMQ
- `docker-compose up test-producer` in order to run the unit testing process

# Comments
If you don't want to use Docker to run the project, you don't have to.  
You can add some environment variables and run the project with sbt. 


| Environment variables        | Description                  |
| ---------------------------- |:----------------------------:|
| `FILE_TO_PROCESS`            | path to the file             |
| `RABBITMQ_USERNAME`          | username of RabbitMQ account |
| `RABBITMQ_PASSWORD`          | password of RabbitMQ account |
| `RABBITMQ_HOST`              | hostname to contact RabbitMQ |
| `RABBITMQ_PORT`              | port to contact RabbitMQ     |