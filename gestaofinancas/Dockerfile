# Usa a imagem oficial do OpenJDK como base
FROM openjdk:17-alpine

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR do seu projeto para o contêiner
COPY target/gestaofinancas-0.0.1-SNAPSHOT.jar /app/gestaofinancas.jar

# Comando a ser executado quando o contêiner for iniciado
CMD ["java", "-jar", "gestaofinancas.jar"]
