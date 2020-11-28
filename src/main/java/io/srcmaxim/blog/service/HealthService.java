package io.srcmaxim.blog.service;

import io.srcmaxim.blog.model.dynamodb.DynamoDb;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class HealthService {

    @Inject
    DynamoDbClient dynamoDB;

    public Map<String, Object> getHealth() {
        try {
            dynamoDB.describeTable(describeTableRequest(DynamoDb.Blog.TABLE_NAME));
            return Map.of(
                    "status", 200,
                    "data", Map.of(
                    "status", "UP",
                    "checks", List.of(Map.of(
                            "name", "DynamoDB",
                            "status", "UP"
                    ))
            ));
        } catch (Exception e) {
            return Map.of(
                    "status", 500,
                    "data", Map.of(
                    "status", "DOWN",
                    "checks", List.of(Map.of(
                            "name", "DynamoDB",
                            "status", "DOWN"
                    ))
            ));
        }
    }

    protected DescribeTableRequest describeTableRequest(String tableName) {
        return  DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
    }

}
