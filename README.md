# Sprint - 2
Um dos objetivos da Sprint 2, em relação a POC, era colocar mais algumas ferramentas em pratica, abaixo os conceitos que foram aplicados:
- Integrar os microserviços desenvolvidos com o banco de dados
- Realizar Tracing
- Ter cenarios de teste e logs

# Modificações na arquitetura principal
Não houve mudanças relevantes em relação ao primeiro desenho proposto, abaixo os detalhes das modificações realizadas:
- O banco Postgresql foi substituido pelo banco DynamoDB

![image](https://user-images.githubusercontent.com/100853329/236307534-a50332cf-e668-4c54-b5c8-cb8a2cc7092a.png)

- Para realização de tracing, foi adicionado ao projeto o Jaeger

![image](https://user-images.githubusercontent.com/100853329/236309783-3ac77f37-0119-40ac-8f90-9c89d6c3c7ad.png)

## Docker Compose
Para rodar o projeto, deve-se rodar o compose abaixo:
```
version: '3'

services:   
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

#Implementando Jaeger
Para fazer a implementação do Jaeger, bastas adicionar as dependências de opentrancing no projeto, adicionar a configuração do Jaeger no properties e inserir a anotação "@Traced" nas classes que serão observadas (normalmente é inserido nos services), abaixo detalhes:
- Dependências utilizadas
```
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-opentracing</artifactId>
</dependency>
```
- As configurações que são utilizadas no application.properties, são padrões em todo projeto, devendo somente incluir o nome do microserviço
```
# open-tracing
quarkus.jaeger.service-name = <NOME_DO_MICRO_SERVIÇO>
quarkus.jaeger.sampler-type = const
quarkus.jaeger.sampler-param = 1
quarkus.log.console.format = %d{HH:mm:ss} %-5p traceId=%X{traceId}, parentId=%X{parentId}, spanId=%X{spanId}, sampled=%X{sampled} [%c{2.}] (%t) %s%e%n
```

# Implementando testes unitários

## Prints do funcionamento da aplicação




