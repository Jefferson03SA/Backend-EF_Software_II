package com.paygrid.dockerized.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;

@Configuration
public class AWSConfig {

    @Value("${aws.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.secretKey:}")
    private String secretKey;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Bean
    public TextractClient textractClient() {
        // Si no hay credenciales configuradas, usar el perfil por defecto
        if (accessKeyId.isEmpty() || secretKey.isEmpty()) {
            return TextractClient.builder()
                    .region(Region.of(region))
                    .build();
        } else {
            // Usar credenciales específicas
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
            return TextractClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build();
        }
    }
} 