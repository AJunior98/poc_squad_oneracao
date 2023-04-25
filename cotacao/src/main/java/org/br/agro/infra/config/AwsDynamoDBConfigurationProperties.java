package org.br.agro.infra.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.dynamodb")
public interface AwsDynamoDBConfigurationProperties {

    @WithName("endpoint-override")
    String endpoint();

    @WithName("aws.region")
    String region();

    @WithName("aws.credentials")
    DynamoDbCredentialsProperties credentials();


    interface DynamoDbCredentialsProperties {
        @WithName("static-provider.access-key-id")
        String accessKey();

        @WithName("static-provider.secret-access-key")
        String secretKey();
    }


}
