package io.srcmaxim.blog.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.srcmaxim.blog.model.dynamodb.DynamoDb;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@QuarkusTest
class HealthServiceTest {

    @InjectMock
    DynamoDbClient dynamoDB;

    @Inject
    HealthService testee;

    @Test
    void getHealth_WhenDynamoDbIsUp_ThenReturn200() {
        DescribeTableRequest describeTableRequest = DescribeTableRequest.builder().tableName(DynamoDb.Blog.TABLE_NAME).build();
        when(dynamoDB.describeTable(describeTableRequest))
                .thenReturn(null);

        Map<String, Object> actual = testee.getHealth();

        assertThat(actual.get("status")).isEqualTo(200);
        assertThat(actual.get("data")).isEqualTo(Map.of(
                "status", "UP",
                "checks", List.of(Map.of(
                        "name", "DynamoDB",
                        "status", "UP"
                ))));
    }

    @Test
    void getHealth_WhenDynamoDbIsDown_ThenReturn500() {
        DescribeTableRequest describeTableRequest = DescribeTableRequest.builder().tableName(DynamoDb.Blog.TABLE_NAME).build();
        when(dynamoDB.describeTable(describeTableRequest))
                .thenThrow(new RuntimeException());

        Map<String, Object> actual = testee.getHealth();

        assertThat(actual.get("status")).isEqualTo(500);
        assertThat(actual.get("data")).isEqualTo(Map.of(
                "status", "DOWN",
                "checks", List.of(Map.of(
                        "name", "DynamoDB",
                        "status", "DOWN"
                ))));
    }

}
