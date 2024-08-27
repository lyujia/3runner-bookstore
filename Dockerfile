FROM eclipse-temurin:21-jre

WORKDIR /bookstore

COPY ./target/bookstore.jar /bookstore/bookstore.jar

EXPOSE 8081 8082

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Run the entrypoint script
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]