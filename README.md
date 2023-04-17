# Sistema de gerenciamente de agronegocio - Squad Oneração (Proof of Concept)
Está POC tem como objetivo colocar em pratica algumas possiveis ferramentas que serão utilizadas no decorrer do desenvolvimento do microserviço de Gestão de Risco/Oneração.

## Requisitos funcionais

A AJAgro é uma empresa brasileira no ramo agropecuario que vende seus produtos para China, Europa e EUA. Sabendo disso, a empresa está modernizando seus sistemas e adaptando-os para o cloud e surgiu a necessidade de construir um sistema onde permita receber novas ofertas de compra de clientes, analisar o câmbio do par de moedas Real Brasileiro em comparação com o Dólar Americano e criar a partir disso oportunidades de venda de seus produtos Agro.

Essas Oportunidades de venda devem ser acessadas diretamente via API Rest e também devem gerar relatorios no formato CSV para futuras análises.

O fluxo basico é:

1 - Acompanhamento da cotação do dólar americano. Se o dólar estiver valorizando e houver sequências de valorização da moeda americana, envia esta informação atualizada para o banco de dados e considera esse valor atual do dólar na criação de uma nova oferta.

2 - Entrada de novas ofestas de compra por parte dos cliente deve conter os seguintes dados: Nome da empresa, valor oferecido, quantas toneladas, produto, país de origem, validade da proposta e data da criação da proposta.

3 - Regra sobre propostas:
  - Apenas usuários do tipo cliente pode inserir novas ofertas no sistema.
  - Um operador pode consultar detalhes das ofertas mas não pode deletar ofertas.
  - Um usuário gerente pode consultar detalhes e também deleter propostas.
  
4 - Com as informações de novas ofertas e do câmbio atual, são criadas Oportunidades de Venda que ficam acessíveis aos operadores da AJ Agro por formato JSON ou Arquivos CSV.

## Requisitos técnicos

1 - Essa aplicação é projetada para ser utilizada em um horizonte de médio a longo prazo, funcionando 24 horas por dia. Portanto, é imperativo que ela seja escalável e altamente disponível.

2 - A equipe de tecnologia da AJ Agro pretende migrar suas aplicações para a plataforma de nuvem e desenvolver novas API's e aplicações prontas para operar em um ambiente de computação em nuvem.

## Desenho de arquitetura proposto

![image](https://user-images.githubusercontent.com/100853329/232250163-3c264a86-ef01-458a-815e-cae895b6f4be.png)

# Arquivos e configurações

Abaixo alguns arquivos e configurações que foram utilizados no projeto.

## Docker Compose

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
    networks:
      - broker-kafka
      
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

networks:
  broker-kafka:
    driver: bridge
```
# Collection do Postman

```
{"collection":{"info":{"_postman_id":"eb63ed7d-0d61-497f-b43f-5b80f2029890","name":"AJ Agro","schema":"https://schema.getpostman.com/json/collection/v2.1.0/collection.json","updatedAt":"2023-04-15T19:30:49.000Z","uid":"20966486-eb63ed7d-0d61-497f-b43f-5b80f2029890"},"item":[{"name":"Gateway","item":[{"name":"Offer","item":[{"name":"newOffer","id":"b262f861-acc8-4d63-8007-195d6477d602","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"POST","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODEzNDExNjksImlhdCI6MTY4MTM0MDg2OSwianRpIjoiMjA4ZDFmNGMtMzlmZS00ODA1LWE2NmYtOGQwOWIyZjhkNWVjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWZmMjE1OTgtOTYwMC00ZWNlLTgxMmMtMjJiMWI2OWQwOTA2IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6Ijk5ZGM3MDY4LTk2YWUtNGEyNi05NTJiLTQ1M2Q5NmI4ZmQ4NiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsicHJvcG9zYWwtY3VzdG9tZXIiLCJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtcXVhcmt1cyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiOTlkYzcwNjgtOTZhZS00YTI2LTk1MmItNDUzZDk2YjhmZDg2IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiR3JvdXAiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzaW5vY2hlbSIsImZhbWlseV9uYW1lIjoiR3JvdXAifQ.blTbVZL8faVJUak7Z0QSpV5elOhjN6Y-2MbZG0qf_pkv2M4BLHNSev43sO7W_8iR9_kQFDdYO8tp-2vx9WnaiK27qrMRMs4Egt_rYhdh8PYMSLRRnSIvklW1LKgTGNIR0sHuW0FZNnkQq_ajVuAZ5pp-frV3aWTCqIwCwbz7KikqDNsRlOeEQuqN3l1n437Jl5cSsYrsaZ8oJrtmBK9-w-7UQT1-SLXOXbkCmCjmG3HCTM-ne05qn2rqZR0PKLZ37jbOHFZZRG_ZWLCCNDadGfHuLIa3pSU92KezaAKnVhR8ctwfpyKWSQwIkuR_kXxfnMYwEzEAd6q_5QInTAeXcA","type":"text"}],"body":{"mode":"raw","raw":"{\r\n    \"customer\": \"Sinochem Group\",\r\n    \"priceTonne\": 1900,\r\n    \"product\": \"Arroz\",\r\n    \"tonnes\": 2000,\r\n    \"country\": \"China\",\r\n    \"proposalValidityDays\": 2\r\n}","options":{"raw":{"language":"json"}}},"url":{"raw":"{{gateway}}/api/trade","host":["{{gateway}}"],"path":["api","trade"]}},"response":[],"uid":"20966486-b262f861-acc8-4d63-8007-195d6477d602"},{"name":"getOfferDetailsById","id":"a67a3f8b-d94e-4416-9624-9fb20c2d21b8","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"GET","header":[],"body":{"mode":"formdata","formdata":[]},"url":{"raw":"{{gateway}}/api/trade/{{id}}","host":["{{gateway}}"],"path":["api","trade","{{id}}"]}},"response":[],"uid":"20966486-a67a3f8b-d94e-4416-9624-9fb20c2d21b8"},{"name":"removeOffer","id":"b1dfe4f3-4424-4415-b363-cceefc0a1622","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"DELETE","header":[],"body":{"mode":"formdata","formdata":[]},"url":{"raw":"{{gateway}}/api/trade/remove/{{id}}","host":["{{gateway}}"],"path":["api","trade","remove","{{id}}"]}},"response":[],"uid":"20966486-b1dfe4f3-4424-4415-b363-cceefc0a1622"}],"id":"6bc48507-d182-4834-bf97-ad154ae2ed6e","uid":"20966486-6bc48507-d182-4834-bf97-ad154ae2ed6e"},{"name":"Report","item":[{"name":"requestReport","id":"bd3afd19-706f-4152-b0cc-ed6cb408745f","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"GET","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODEzNDEyNDcsImlhdCI6MTY4MTM0MDk0NywianRpIjoiOGJmMjAxMGQtYWRkMi00MTllLTg2YTktOTAzYTgyODI2YTZjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzFlMjBkYTItMWI0Zi00NDY3LTgwNzMtZTRmMjZjMGIzMzAzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjllMjA3NmFjLWM0ZDEtNDRkMS1iZDU0LTFkZjY3MjdhYWM1OCIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLXF1YXJrdXMiLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiI5ZTIwNzZhYy1jNGQxLTQ0ZDEtYmQ1NC0xZGY2NzI3YWFjNTgiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJzaWx2YSIsInByZWZlcnJlZF91c2VybmFtZSI6Impvc2UiLCJmYW1pbHlfbmFtZSI6InNpbHZhIn0.mt_9yzVnq_5ddB1Asq8qa8HURlEpdXSpjoWHEQ_R4_goi8ymFhDS1E3Ph9RdVMIqD1Inn0OsSLm8hLptsl6tMVJhlKn2eFIyOwWoJkV1vNl56WBQxOjcx7ojJTIhNXzyPMgsvW324sMLkR5JqjcsC-4OIAlba5vhRv8dAvhZ-cfLmX7xRcL5PXqkoNeTebF00s48SJlkOKsQ4adXt-z1wIjUQmfIWRwTQBz8JSa8MSWbiVSPxEk1Qf8pUCwNYjdrU-47hWeQeA--xfwJhel2qhdomAv2ovApHVP2YznDIq7pKaUsPxDa6EqE-NE0px5G83gmkOMzGRAq0OL3RCtNLA","type":"text"}],"body":{"mode":"formdata","formdata":[]},"url":{"raw":"{{gateway}}/api/opportunity/data","host":["{{gateway}}"],"path":["api","opportunity","data"]}},"response":[],"uid":"20966486-bd3afd19-706f-4152-b0cc-ed6cb408745f"},{"name":"generateReport","id":"98aadcb8-6945-4e08-9ab8-db53730ba6d7","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"GET","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODEzNDEyNDcsImlhdCI6MTY4MTM0MDk0NywianRpIjoiOGJmMjAxMGQtYWRkMi00MTllLTg2YTktOTAzYTgyODI2YTZjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzFlMjBkYTItMWI0Zi00NDY3LTgwNzMtZTRmMjZjMGIzMzAzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjllMjA3NmFjLWM0ZDEtNDRkMS1iZDU0LTFkZjY3MjdhYWM1OCIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLXF1YXJrdXMiLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiI5ZTIwNzZhYy1jNGQxLTQ0ZDEtYmQ1NC0xZGY2NzI3YWFjNTgiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJzaWx2YSIsInByZWZlcnJlZF91c2VybmFtZSI6Impvc2UiLCJmYW1pbHlfbmFtZSI6InNpbHZhIn0.mt_9yzVnq_5ddB1Asq8qa8HURlEpdXSpjoWHEQ_R4_goi8ymFhDS1E3Ph9RdVMIqD1Inn0OsSLm8hLptsl6tMVJhlKn2eFIyOwWoJkV1vNl56WBQxOjcx7ojJTIhNXzyPMgsvW324sMLkR5JqjcsC-4OIAlba5vhRv8dAvhZ-cfLmX7xRcL5PXqkoNeTebF00s48SJlkOKsQ4adXt-z1wIjUQmfIWRwTQBz8JSa8MSWbiVSPxEk1Qf8pUCwNYjdrU-47hWeQeA--xfwJhel2qhdomAv2ovApHVP2YznDIq7pKaUsPxDa6EqE-NE0px5G83gmkOMzGRAq0OL3RCtNLA","type":"text"}],"body":{"mode":"formdata","formdata":[]},"url":{"raw":"{{gateway}}/api/opportunity/report","host":["{{gateway}}"],"path":["api","opportunity","report"]}},"response":[],"uid":"20966486-98aadcb8-6945-4e08-9ab8-db53730ba6d7"}],"id":"98e2f899-f4f2-4a85-a8aa-2b8e4e965301","uid":"20966486-98e2f899-f4f2-4a85-a8aa-2b8e4e965301"}],"id":"55a02210-7ad0-4bc1-9ac2-574e65b45860","uid":"20966486-55a02210-7ad0-4bc1-9ac2-574e65b45860"},{"name":"obterToken","id":"85da949c-5ce6-4314-af28-59cea523daa7","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"auth":{"type":"basic","basic":[{"key":"password","value":"secret","type":"string"},{"key":"username","value":"backend-service","type":"string"}]},"method":"POST","header":[],"body":{"mode":"urlencoded","urlencoded":[{"key":"username","value":"jose","type":"text"},{"key":"password","value":"1234","type":"text"},{"key":"grant_type","value":"password","type":"text"}]},"url":{"raw":"{{token}}/token","host":["{{token}}"],"path":["token"]}},"response":[],"uid":"20966486-85da949c-5ce6-4314-af28-59cea523daa7"}]}}
```

