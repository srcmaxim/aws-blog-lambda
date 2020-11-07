package io.srcmaxim.blog.repository;

import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.dynamodb.DynamoDb;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.InternalServerErrorException;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.RequestLimitExceededException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

class AbstractDynamoDbRepository {

    protected PutItemRequest putRequest(Supplier<Map<String, AttributeValue>> item) {
        return PutItemRequest.builder()
                .tableName(DynamoDb.Blog.TABLE_NAME)
                .item(item.get())
                .build();
    }

    protected QueryRequest queryRequest(String id) {
        return QueryRequest.builder()
                .tableName(DynamoDb.Blog.TABLE_NAME)
                .keyConditionExpression("PK = :pk")
                .consistentRead(false)
                .scanIndexForward(true)
                .expressionAttributeValues(Map.of(
                        ":pk", AttributeValue.builder().s("POST#" + id).build()
                )).build();
    }

    protected ScanRequest scanRequest() {
        return ScanRequest.builder()
                .tableName("Blog")
                .consistentRead(false)
                .build();
    }

    protected QueryRequest queryByTagRequest(String tag) {
        return QueryRequest.builder()
                .tableName(DynamoDb.Blog.TABLE_NAME)
                .indexName(DynamoDb.BlogGSI1.GSI1_NAME)
                .keyConditionExpression("GSI1PK = :pk")
                .consistentRead(false)
                .scanIndexForward(false)
                .expressionAttributeValues(Map.of(
                        ":pk", AttributeValue.builder().s("TAG#" + tag).build()
                ))
                .build();
    }

    protected List<DeleteItemRequest> deleteRequest(Post post) {
        var deleteItemRequests = new ArrayList<DeleteItemRequest>();
        String pk = "POST#" + post.id;
        deleteItemRequests.add(deleteRequest(pk, "POST"));
        for (int i = 0; i < post.tags.size(); i++) {
            deleteItemRequests.add(deleteRequest(pk, "TAG#" + i));
        }
        for (int i = 0; i < post.messages.size(); i++) {
            deleteItemRequests.add(deleteRequest(pk, "MSG#" + post.messages.get(i).createdAt));
        }
        return deleteItemRequests;
    }

    private DeleteItemRequest deleteRequest(String pk, String sk) {
        return DeleteItemRequest.builder()
                .tableName(DynamoDb.Blog.TABLE_NAME)
                .key(Map.of(
                        DynamoDb.Blog.PK, AttributeValue.builder().s(pk).build(),
                        DynamoDb.Blog.SK, AttributeValue.builder().s(sk).build()
                ))
                .build();
    }

    protected static void handleCommonErrors(Exception exception) {
        try {
            throw exception;
        } catch (InternalServerErrorException isee) {
            System.out.println("Internal Server Error, generally safe to retry with exponential back-off. Error: " + isee.getMessage());
        } catch (RequestLimitExceededException rlee) {
            System.out.println("Throughput exceeds the current throughput limit for your account, increase account level throughput before " +
                    "retrying. Error: " + rlee.getMessage());
        } catch (ProvisionedThroughputExceededException ptee) {
            System.out.println("Request rate is too high. If you're using a custom retry strategy make sure to retry with exponential back-off. " +
                    "Otherwise consider reducing frequency of requests or increasing provisioned capacity for your table or secondary index. Error: " +
                    ptee.getMessage());
        } catch (ResourceNotFoundException rnfe) {
            System.out.println("One of the tables was not found, verify table exists before retrying. Error: " + rnfe.getMessage());
        } catch (Exception e) {
            System.out.println("An exception occurred, investigate and configure retry strategy. Error: " + e.getMessage());
        }
    }

}
