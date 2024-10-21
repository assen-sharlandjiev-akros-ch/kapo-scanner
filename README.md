

To create the docker image, run the following maven goal:

```
./mvnw spring-boot:build-image
```

To run the scanner application execute:

```
docker run --rm -v $HOME/Downloads/source:/source -v $HOME/Downloads/target:/target  docker.io/library/kapo-scanner:0.0.1-SNAPSHOT
```

