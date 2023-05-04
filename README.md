# Sprint - 2
Um dos objetivos da Sprint 2, em relação a POC, era colocar mais algumas ferramentas em pratica, abaixo os conceitos que foram aplicados:
- Integrar os microserviços desenvolvidos com o banco de dados
- Realizar Tracing
- Ter alguns cenarios de teste e logs

# Desenho de arquitetura proposto

![image](https://user-images.githubusercontent.com/100853329/236301045-0b44b674-6c09-4a3e-9385-aa52d5e93c12.png)

## Docker Compose
Para rodar o projeto, deve-se rodar o compose abaixo:
```
version: '3'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: zookeeper
    networks:
      - broker-kafka
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:latest
    container_name: kafka
    networks:
      - broker-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  kafdrop:
    image: obsidiandynamics/kafdrop:latest
    container_name: kafdrop
    networks:
      - broker-kafka
    depends_on:
      - kafka
    ports:
      - "19000:9000"
    environment:
      KAFKA_BROKERCONNECT: kafka:29092

  postgres:
    image: 'postgres:13.1-alpine'
    container_name: postgres
    ports:
      - 5432:5432
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=1234
      
  keycloak:
    image: jboss/keycloak
    container_name: keycloak
    environment:
      - KEYCLOAK_USER=admin
      - KEYCLOAK_PASSWORD=admin
    ports:
      - "8180:8080"
      - "8443:8443"
    volumes:
      - ./data:/opt/jboss/keycloak/standalone/data
   
  jaeger:
    image: jaegertracing/all-in-one
    container_name: jaeger
    ports:
      - "16686:16686" # Porta para o painel de visualização do Jaeger
      - "6831:6831/udp" # Porta para o agente do Jaeger
    restart: always

  localstack:
    image: localstack/localstack
    container_name: localstack
    ports:
      - "4566:4566"
    environment:
      - SERVICES=dynamodb
      - DEBUG=1
      - DATA_DIR=/tmp/localstack/data
    volumes:
      - "./localstack:/tmp/localstack"

networks:
  broker-kafka:
    driver: bridge
```
# Implementando LocalStack
A ideia desta Issue, era implementar o DynamoDB utilizando o LocalStack, abaixo o passo a passo de como deve ser feito para utilização:

- Deve-se ter o Docker instalado e para facilitar a utilização do Docker, recomendo a instalação do Docker Desktop
- Instala o AWS CLI para que seja possivel utilizar os comando "aws" na sua maquina local
- Instale o Python em sua maquina e logo em seguida utlizando o pip instale o awscli-local utilizando o comando:

Antes de utilizar o comando abaixo: aws --endpoint http://localhost:4566 --profile localstack s3 mb s3://oneracao
```
pip install awscli-local
```
Após utilizar o comando abaixo: awslocal s3 mb s3://oneracao

- Para utilizar o DynamoDB, utilize o compose abaixo (o documento abaixo está presente o arquivo "docker-compose.yaml" do projeto, mas caso queira rodar de maneira apartada, utilize o compose abaixo.)
```
services:
  localstack:
    image: localstack/localstack
    container_name: localstack
    ports:
      - "4566:4566"
    environment:
      - SERVICES=dynamodb
      - DEBUG=1
      - DATA_DIR=/tmp/localstack/data
    volumes:
      - "./localstack:/tmp/localstack"
```
- Para criar uma tabela, siga o comando abaixo:

```
awslocal dynamodb create-table --table-name <NOME_DA_SUA_TABELA> \
                          --attribute-definitions AttributeName=<NOME_DO_SEU_IDENTIFICADOR_PRIMARIO>,AttributeType=S \
                          --key-schema AttributeName=<NOME_DO_SEU_IDENTIFICADOR_PRIMARIO>,KeyType=HASH \
                          --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
```

- Para validar se deu certo, utilize o comando abaixo e verifique se a tabela que criou aparece.
```
awslocal dynamodb list-tables
```

## Comando adicionais
- Comando para apagar uma tabela
```
awslocal dynamodb delete-table --table-name <NOME_DA_SUA_TABELA>
```
- Comando para verificar os documentos dentro da tabela
```
awslocal dynamodb scan --table-name <NOME_DA_SUA_TABELA> --limit 10
```

## POM - Dependências e configs do DynamoDB
Abaixo as dependências, repositorio e configurações utilizadas.
```
<properties>
  <awssdk.version>1.12.201</awssdk.version>
  <dynamodblocal.version>1.15.0</dynamodblocal.version>
</properties>

<dependencyManagement>
  <dependencies>
      <dependency>
          <groupId>com.amazonaws</groupId>
          <artifactId>aws-java-sdk-bom</artifactId>
          <version>${awssdk.version}</version>
          <type>pom</type>
          <scope>import</scope>
      </dependency>
  </dependencies>
</dependencyManagement>
<dependencies>
    <dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>aws-java-sdk-dynamodb</artifactId>
    </dependency>
    <dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>DynamoDBLocal</artifactId>
        <version>${dynamodblocal.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-apache-httpclient</artifactId>
    </dependency>
    <dependency>
        <groupId>org.modelmapper</groupId>
        <artifactId>modelmapper</artifactId>
        <version>${modelmapper.version}</version>
    </dependency>
</dependencies>
<repositories>
    <repository>
        <id>dynamodb-local</id>
        <name>DynamoDB Local Release Repository</name>
        <url>https://s3-us-west-2.amazonaws.com/dynamodb-local/release</url>
    </repository>
</repositories>
```

## Como implementar o DynamoDB no código?
Para facilitar a vida de quem quer entender como funciona, a arquitetura utilizada para conectar no Dynamo está no pacote de Infra do microserviço de cotação, todas as anotações e configurações da AWS estão presentes neste pacote.

## Prints do funcionamento da aplicação




