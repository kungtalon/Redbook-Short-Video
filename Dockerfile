FROM adoptopenjdk/openjdk8
WORKDIR /red-book-video-application
COPY book-api/target/classes ./
ADD book-api/target/book-api.jar ./
CMD ["java", "-jar", "book-api.jar", "-Dspring.profiles.active=remote"]
