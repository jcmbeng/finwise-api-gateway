# Utiliser une image Java 21 (ou version compatible)
FROM openjdk:21-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier jar de l’application dans le conteneur
COPY target/*.jar /app/finwise-api-gateway.jar
#COPY src/main/resources/local-ssl.p12 /app/local-ssl.p12

# Exposer le port sur lequel l’application écoute
EXPOSE 9903

# Commande pour exécuter l’application Spring Boot
ENTRYPOINT ["java", "-jar", "/app/finwise-api-gateway.jar"]
