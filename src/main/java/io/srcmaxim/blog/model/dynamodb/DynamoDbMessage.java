package io.srcmaxim.blog.model.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class DynamoDbMessage {

    public String pk;
    public String sk;
    public String message;
    public String userEmail;

    public static DynamoDbMessage from(Map<String, AttributeValue> item) {
        DynamoDbMessage dynamoDbTag = new DynamoDbMessage();
        dynamoDbTag.pk = item.get(DynamoDb.Blog.PK).s();
        dynamoDbTag.sk = item.get(DynamoDb.Blog.SK).s();
        dynamoDbTag.message = item.get(DynamoDb.Blog.message).s();
        dynamoDbTag.userEmail = item.get(DynamoDb.Blog.userEmail).s();
        return dynamoDbTag;
    }

    public Map<String, AttributeValue> to() {
        return Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s(pk).build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s(sk).build(),
                DynamoDb.Blog.message, AttributeValue.builder().s(message).build(),
                DynamoDb.Blog.userEmail, AttributeValue.builder().s(userEmail).build()
        );
    }
    
}
