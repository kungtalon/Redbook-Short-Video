FROM adoptopenjdk/openjdk8
WORKDIR /app
ADD book-api/target/book-api.jar ./
CMD ["java", "-jar", "book-api.jar", "--spring.profiles.active=remote"]
