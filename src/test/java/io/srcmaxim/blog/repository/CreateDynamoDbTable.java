package io.srcmaxim.blog.repository;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateDynamoDbTable {

    private static final Logger LOGGER = Logger.getLogger(CreateDynamoDbTable.class);

    @Inject
    DynamoDbClient dynamoDb;

    @ConfigProperty(name = "quarkus.dynamodb.endpoint-override")
    String dynamoDbEndpointUrl;

    @PostConstruct
    void onStart() {
        LOGGER.info("Creating DynamoDB Local...");
        LOGGER.info(DynamoDbLocalResource.LOGGER);
        LOGGER.infov("DynamoDB Local Endpoint URL: [{0}]", dynamoDbEndpointUrl);
        LOGGER.info("Creating DynamoDB Local Table...");
        CreateTableResponse table = dynamoDb.createTable(createTableRequest());
        TableDescription tableDescription = table.tableDescription();
        LOGGER.infov("Created DynamoDB Local Table: [{0}] at [{1}]",
                tableDescription.tableArn(), tableDescription.creationDateTime());
        tableDescription.globalSecondaryIndexes().forEach(globalSecondaryIndexDescription -> {
            LOGGER.infov("Created DynamoDB Local GSI Table: [{0}] at [{1}]",
                    tableDescription.tableArn(), tableDescription.creationDateTime());
        });
    }

    private CreateTableRequest createTableRequest() {
        AttributeDefinition pk = AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build();
        AttributeDefinition sk = AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build();
        AttributeDefinition gsi1Pk = AttributeDefinition.builder().attributeName("GSI1PK").attributeType(ScalarAttributeType.S).build();
        AttributeDefinition gsi1Sk = AttributeDefinition.builder().attributeName("GSI1SK").attributeType(ScalarAttributeType.S).build();
        KeySchemaElement pkKey = KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build();
        KeySchemaElement skKey = KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build();
        ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build();
        return CreateTableRequest.builder()
                .tableName("Blog")
                .attributeDefinitions(pk, sk, gsi1Pk, gsi1Sk)
                .keySchema(pkKey, skKey)
                .provisionedThroughput(provisionedThroughput)
                .globalSecondaryIndexes(createGsi1Request())
                .build();
    }

    private GlobalSecondaryIndex createGsi1Request() {
        KeySchemaElement gsi1PkKey = KeySchemaElement.builder().attributeName("GSI1PK").keyType(KeyType.HASH).build();
        KeySchemaElement gsi1SkKey = KeySchemaElement.builder().attributeName("GSI1SK").keyType(KeyType.RANGE).build();
        ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build();
        return GlobalSecondaryIndex.builder()
                .indexName("GSI1")
                .keySchema(gsi1PkKey, gsi1SkKey)
                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                .provisionedThroughput(provisionedThroughput)
                .build();
    }

}
