# Sistema de gerenciamente de agronegocio - Squad Oneração (Proof of Concept)
Está POC tem como objetivo colocar em pratica algumas possiveis ferramentas que serão utilizadas no decorrer do desenvolvimento do microserviço de Gestão de Risco/Oneração.

## Requisitos funcionais

A AJAgro é uma empresa brasileira no ramo agropecuario que vende seus produtos para China, Europa e EUA. Sabendo disso, a empresa está modernizando seus sistemas e adaptando-os para o cloud e surgiu a necessidade de construir um sistema onde permita receber novas ofertas de compra de clientes, analisar o câmbio do par de moedas Real Brasileiro em comparação com o Dólar Americano e criar a partir disso oportunidades de venda de seus produtos Agro.

Essas Oportunidades de venda devem ser acessadas diretamente via API Rest e também devem gerar relatorios no formato CSV para futuras análises.

O fluxo basico é:

1 - Acompanhamento da cotação do dólar americano. Se o dólar estiver valorizando e houver sequências de valorização da moeda americana, envia esta informação atualizada para o banco de dados e considera esse valor atual do dólar na criação de uma nova oferta.

2 - Entrada de novas propostas de compra por parte dos cliente deve conter os seguintes dados: Nome da empresa, valor oferecido, quantas toneladas, produto, país de origem, validade da proposta e data da criação da proposta.

3 - Regra sobre propostas:
  - Apenas usuários do tipo cliente pode inserir novas propostas no sistema.
  - Um operador pode consultar detalhes das propostas mas não pode deletar propostas.
  - Um usuário gerente pode consultar detalhes e também deleter propostas.
  
4 - Com as informações de novas propostas e do câmbio atual, são criadas oportunidades de venda que ficam acessíveis aos operadores da AJ Agro por formato JSON ou Arquivos CSV.

## Requisitos técnicos

1 - Essa aplicação é projetada para ser utilizada em um horizonte de médio a longo prazo, funcionando 24 horas por dia. Portanto, é imperativo que ela seja escalável e altamente disponível.

2 - A equipe de tecnologia da AJ Agro pretende migrar suas aplicações para a plataforma de nuvem e desenvolver novas API's e aplicações prontas para operar em um ambiente de computação em nuvem.

## Desenho de arquitetura proposto

![img.png](img.png)

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
{"collection":{"info":{"_postman_id":"2983533c-4fc4-4390-b419-29864915760e","name":"AJ Agro Copy","schema":"https://schema.getpostman.com/json/collection/v2.1.0/collection.json","updatedAt":"2023-04-15T19:38:00.000Z","uid":"20966486-2983533c-4fc4-4390-b419-29864915760e"},"item":[{"name":"Gateway","item":[{"name":"Proposta","item":[{"name":"newProposal","id":"5b60637b-3d23-4106-9b87-6f7286b6701a","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"POST","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODE3MzI5NTYsImlhdCI6MTY4MTczMjY1NiwianRpIjoiYjkzN2U2ZWYtMzI1Yi00YTBmLThiMTUtNTU0MjhhYTU1OTg3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWZmMjE1OTgtOTYwMC00ZWNlLTgxMmMtMjJiMWI2OWQwOTA2IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjVlMDhiYmY1LTkzNTUtNDEyMi04YWIyLWYxZGZkNTViY2Q0OSIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsicHJvcG9zYWwtY3VzdG9tZXIiLCJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtcXVhcmt1cyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiNWUwOGJiZjUtOTM1NS00MTIyLThhYjItZjFkZmQ1NWJjZDQ5IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiR3JvdXAiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzaW5vY2hlbSIsImZhbWlseV9uYW1lIjoiR3JvdXAifQ.GDAKYkt8YAoxLMFa2Pr1h8hu07ExhS2_a2qRCvPd-AO0baVUjueH-TVOP9JV9P1XKO8hD2oF9kL2a4FpPXtsRdnB6_sys13b9FECDh1jIO8ZktashsO_qo-PAW7zK8uaf-Lv9d7xCohdOjEnWVrMWfEStAQX2JhS6AN-k0uemmRftt9Y-1XParp_3f4OWhxLGMf8nsw0cFsG7Kh0wVgsR-MzEnfWrkFf5cDF0ng2RI1KctLrLDoo-FJRkTEUPu8vwJ1KIfba5ddIXELQx4Etal3Y759dhJkoeHDVoCVJngJLPz-FuG70N93u3inWlxXXN7bGRIFkyeLix5d9K9M7rQ","type":"text"}],"body":{"mode":"raw","raw":"{\r\n    \"customer\": \"Sinochem Group\",\r\n    \"priceTonne\": 1900,\r\n    \"product\": \"Arroz\",\r\n    \"tonnes\": 2000,\r\n    \"country\": \"China\",\r\n    \"proposalValidityDays\": 2\r\n}","options":{"raw":{"language":"json"}}},"url":{"raw":"{{gateway}}/api/trade","host":["{{gateway}}"],"path":["api","trade"]}},"response":[],"uid":"20966486-5b60637b-3d23-4106-9b87-6f7286b6701a"},{"name":"getProposalDetailsById","id":"f978eeb6-1b59-4d27-99d9-06f5175b902b","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"GET","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODE3MzMwNDQsImlhdCI6MTY4MTczMjc0NCwianRpIjoiNTU5MWI5YmItYzM2OS00ZGI0LTkyNWYtNjI3MDY5ODU4YjU4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiM2U2N2Y2NGEtNWYyZC00OWJmLWI1NWQtNDNmZWQ1Y2QzMmM4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImFkNjE3N2ZmLTY5MDQtNDk0MS1hZGRmLTFlM2NkMGIzOWY5YiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsibWFuYWdlciIsIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1xdWFya3VzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiJhZDYxNzdmZi02OTA0LTQ5NDEtYWRkZi0xZTNjZDBiMzlmOWIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJKdW5pb3IiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbWF1cmkiLCJmYW1pbHlfbmFtZSI6Ikp1bmlvciJ9.Ae3QEXwUjcO8BThKquPgPfFOyf-uOWC2iKQcR3ialNpWCsT7Gh4YDgJTYnsosurSNwzQdvDo_hF0xW3c-ZlNvaZ8B3nxd0-1nUM1R3OW-BUcc8DaJJZxGNqdJ1C9ly1U0wmm_briTwC0uLtDnPyy5O23HrJngAuFMpDkXK0T9kT-vPUwUphDqmU8w0aWPHrqp6xrjBOF62LWejPGJBQXGA2qtpf10NV7J8AiBDwtyHiS38U_NmpURppgerrKcEiPAow7nE3-lWn0kJlOc0Nu_bOACjYGvvMuzhGnPbC4OVRfj4ijWVq8_1QaSGyaxmdMevS3A_qHhGnU1b-hUIskXw","type":"text"}],"body":{"mode":"raw","raw":"","options":{"raw":{"language":"json"}}},"url":{"raw":"{{gateway}}/api/trade/2","host":["{{gateway}}"],"path":["api","trade","2"]}},"response":[],"uid":"20966486-f978eeb6-1b59-4d27-99d9-06f5175b902b"},{"name":"removeProposal","id":"b20e2399-7fdb-4f4a-ad8b-7ded64638c66","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"DELETE","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODE3MzMwNDQsImlhdCI6MTY4MTczMjc0NCwianRpIjoiNTU5MWI5YmItYzM2OS00ZGI0LTkyNWYtNjI3MDY5ODU4YjU4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiM2U2N2Y2NGEtNWYyZC00OWJmLWI1NWQtNDNmZWQ1Y2QzMmM4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImFkNjE3N2ZmLTY5MDQtNDk0MS1hZGRmLTFlM2NkMGIzOWY5YiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsibWFuYWdlciIsIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1xdWFya3VzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiJhZDYxNzdmZi02OTA0LTQ5NDEtYWRkZi0xZTNjZDBiMzlmOWIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJKdW5pb3IiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbWF1cmkiLCJmYW1pbHlfbmFtZSI6Ikp1bmlvciJ9.Ae3QEXwUjcO8BThKquPgPfFOyf-uOWC2iKQcR3ialNpWCsT7Gh4YDgJTYnsosurSNwzQdvDo_hF0xW3c-ZlNvaZ8B3nxd0-1nUM1R3OW-BUcc8DaJJZxGNqdJ1C9ly1U0wmm_briTwC0uLtDnPyy5O23HrJngAuFMpDkXK0T9kT-vPUwUphDqmU8w0aWPHrqp6xrjBOF62LWejPGJBQXGA2qtpf10NV7J8AiBDwtyHiS38U_NmpURppgerrKcEiPAow7nE3-lWn0kJlOc0Nu_bOACjYGvvMuzhGnPbC4OVRfj4ijWVq8_1QaSGyaxmdMevS3A_qHhGnU1b-hUIskXw","type":"text"}],"body":{"mode":"formdata","formdata":[]},"url":{"raw":"{{gateway}}/api/trade/remove/1","host":["{{gateway}}"],"path":["api","trade","remove","1"]}},"response":[],"uid":"20966486-b20e2399-7fdb-4f4a-ad8b-7ded64638c66"}],"id":"5e90963d-d60f-4ac2-839e-1520d4106057","uid":"20966486-5e90963d-d60f-4ac2-839e-1520d4106057"},{"name":"Report","item":[{"name":"requestReport","id":"b3aaca52-53a3-400c-808b-124be71e24c0","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"GET","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODE3MzMwNDQsImlhdCI6MTY4MTczMjc0NCwianRpIjoiNTU5MWI5YmItYzM2OS00ZGI0LTkyNWYtNjI3MDY5ODU4YjU4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiM2U2N2Y2NGEtNWYyZC00OWJmLWI1NWQtNDNmZWQ1Y2QzMmM4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImFkNjE3N2ZmLTY5MDQtNDk0MS1hZGRmLTFlM2NkMGIzOWY5YiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsibWFuYWdlciIsIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1xdWFya3VzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiJhZDYxNzdmZi02OTA0LTQ5NDEtYWRkZi0xZTNjZDBiMzlmOWIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJKdW5pb3IiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbWF1cmkiLCJmYW1pbHlfbmFtZSI6Ikp1bmlvciJ9.Ae3QEXwUjcO8BThKquPgPfFOyf-uOWC2iKQcR3ialNpWCsT7Gh4YDgJTYnsosurSNwzQdvDo_hF0xW3c-ZlNvaZ8B3nxd0-1nUM1R3OW-BUcc8DaJJZxGNqdJ1C9ly1U0wmm_briTwC0uLtDnPyy5O23HrJngAuFMpDkXK0T9kT-vPUwUphDqmU8w0aWPHrqp6xrjBOF62LWejPGJBQXGA2qtpf10NV7J8AiBDwtyHiS38U_NmpURppgerrKcEiPAow7nE3-lWn0kJlOc0Nu_bOACjYGvvMuzhGnPbC4OVRfj4ijWVq8_1QaSGyaxmdMevS3A_qHhGnU1b-hUIskXw","type":"text"}],"body":{"mode":"formdata","formdata":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODE3MzI2MDEsImlhdCI6MTY4MTczMjMwMSwianRpIjoiZjg4M2I3NGYtNWRjMy00YTVmLTk3OTUtNWY2ZGJlZjQ1YmUwIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNzFlMjBkYTItMWI0Zi00NDY3LTgwNzMtZTRmMjZjMGIzMzAzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImRkZDMxNjI1LTA1NTItNDc5Ni04MGViLTc2OTJkYWI4ZjU1YiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLXF1YXJrdXMiLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiJkZGQzMTYyNS0wNTUyLTQ3OTYtODBlYi03NjkyZGFiOGY1NWIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJzaWx2YSIsInByZWZlcnJlZF91c2VybmFtZSI6Impvc2UiLCJmYW1pbHlfbmFtZSI6InNpbHZhIn0.VwQDZC2g9fXrFFenRCjkXZPZ-hsoPE7YXDvG_G1Ez-CJNAEFZd7665lkplqwAqHe-eL9bz2fC_Rl0JlXp9rnLFwFDVB_dmJDOyZmruhJXTIz_uitykzn7TPkbyYU8_vkp0z4gGjSAD9Fs5wKbDHDDgPyNvfG6xmer3fwuPWu1WAUBFdVfxdCq7M8Q_r9tyq6bfrY1unYESdNRYINuMuHYsUEBB9bpOtyr04UkDKreQGGI8AxqwufvRDz-E7dL0_7iRUdGX77mlOK7E5RbHJD5UqDPLrE7wCgiGIEh9886d7OpV_ACqmcp_B9CtxsY_AiQQ-AuLMML0T7D2HKhinkng","type":"text","disabled":true}]},"url":{"raw":"{{gateway}}/api/opportunity/data","host":["{{gateway}}"],"path":["api","opportunity","data"]}},"response":[],"uid":"20966486-b3aaca52-53a3-400c-808b-124be71e24c0"},{"name":"generateReport","id":"262f1ea9-4327-4701-b436-8e7356fc2762","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"method":"GET","header":[{"key":"Authorization","value":"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaXzg2UlhKV0ptMmlkVW5rMUg4WkVXQU1zNm4tZ3c5ZTFoVzk2X25OOVFrIn0.eyJleHAiOjE2ODE3MzMwNDQsImlhdCI6MTY4MTczMjc0NCwianRpIjoiNTU5MWI5YmItYzM2OS00ZGI0LTkyNWYtNjI3MDY5ODU4YjU4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL3F1YXJrdXMiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiM2U2N2Y2NGEtNWYyZC00OWJmLWI1NWQtNDNmZWQ1Y2QzMmM4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImFkNjE3N2ZmLTY5MDQtNDk0MS1hZGRmLTFlM2NkMGIzOWY5YiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsibWFuYWdlciIsIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1xdWFya3VzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiJhZDYxNzdmZi02OTA0LTQ5NDEtYWRkZi0xZTNjZDBiMzlmOWIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJKdW5pb3IiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhbWF1cmkiLCJmYW1pbHlfbmFtZSI6Ikp1bmlvciJ9.Ae3QEXwUjcO8BThKquPgPfFOyf-uOWC2iKQcR3ialNpWCsT7Gh4YDgJTYnsosurSNwzQdvDo_hF0xW3c-ZlNvaZ8B3nxd0-1nUM1R3OW-BUcc8DaJJZxGNqdJ1C9ly1U0wmm_briTwC0uLtDnPyy5O23HrJngAuFMpDkXK0T9kT-vPUwUphDqmU8w0aWPHrqp6xrjBOF62LWejPGJBQXGA2qtpf10NV7J8AiBDwtyHiS38U_NmpURppgerrKcEiPAow7nE3-lWn0kJlOc0Nu_bOACjYGvvMuzhGnPbC4OVRfj4ijWVq8_1QaSGyaxmdMevS3A_qHhGnU1b-hUIskXw","type":"text"}],"body":{"mode":"formdata","formdata":[]},"url":{"raw":"{{gateway}}/api/opportunity/report","host":["{{gateway}}"],"path":["api","opportunity","report"]}},"response":[],"uid":"20966486-262f1ea9-4327-4701-b436-8e7356fc2762"}],"id":"2734c1b8-f432-4385-8a94-27b4e9102d6f","uid":"20966486-2734c1b8-f432-4385-8a94-27b4e9102d6f"}],"id":"7c76c319-58cf-450a-a6f2-b0f771008370","uid":"20966486-7c76c319-58cf-450a-a6f2-b0f771008370"},{"name":"obterToken","id":"41452714-29ac-45fd-9962-5764f9ba9a17","protocolProfileBehavior":{"disableBodyPruning":true},"request":{"auth":{"type":"basic","basic":[{"key":"password","value":"secret","type":"string"},{"key":"username","value":"backend-service","type":"string"}]},"method":"POST","header":[],"body":{"mode":"urlencoded","urlencoded":[{"key":"username","value":"amauri","type":"text"},{"key":"password","value":"1234","type":"text"},{"key":"grant_type","value":"password","type":"text"}]},"url":{"raw":"{{token}}/token","host":["{{token}}"],"path":["token"]}},"response":[],"uid":"20966486-41452714-29ac-45fd-9962-5764f9ba9a17"}]}}
```