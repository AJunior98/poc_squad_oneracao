# Issue2# - Implementando LocalStack
A ideia desta Issue, era implementar o DynamoDB utilizando o LocalStack, abaixo o passo a passo de como deve ser feito para utilização:

- Deve-se ter o Docker instalado e para facilitar a utilização do Docker, recomendo a instalação do Docker Desktop
- Instala o AWS CLI para que seja possivel utilizar os comando "aws" na sua maquina local
- Instale o Python em sua maquina e logo em seguida utlizando o pip instale o awscli-local utilizando o comando:

Antes de utilizar o comando abaixo: aws --endpoint http://localhost:4566 --profile localstack s3 mb s3://oneracao
```
pip install awscli-local
```
Após utilizar o comando abaixo: awslocal s3 mb s3://oneracao

- Para utilizar o DynamoDB, utilize o compose abaixo (disponibilizado pelo github do LocalStack)
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

# Como configurar no POM
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

# Como fica no código?
Para facilitar a vida de quem quer entender como funciona, a arquitetura utilizada para conectar no Dynamo está no pacote de Infra, todas as anotações e configurações da AWS estão presentes neste pacote.




