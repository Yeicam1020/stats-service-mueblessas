package com.mueblessas.stats.dynamodb.config;

import com.mueblessas.stats.dynamodb.StatisticsDynamoEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbEnhancedAsyncClient enhancedClient() {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(
                        DynamoDbAsyncClient.builder()
                                .endpointOverride(URI.create("http://localhost:8000"))
                                .region(Region.US_EAST_1)
                                .credentialsProvider(
                                        StaticCredentialsProvider.create(
                                                AwsBasicCredentials.create("dummy", "dummy")
                                        )
                                )
                                .build()
                )
                .build();
    }

    @Bean
    public DynamoDbAsyncTable<StatisticsDynamoEntity> statsTable(DynamoDbEnhancedAsyncClient client) {
        return client.table("stats-table", TableSchema.fromBean(StatisticsDynamoEntity.class));
    }
}
