package io.srcmaxim.blog.model.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class DynamoDbTag {

    public String pk;
    public String sk;
    public String gsi1Pk;
    public String title;
    public String gsi1Sk;
    public int readMinutes;

    public static DynamoDbTag from(Map<String, AttributeValue> item) {
        var dynamoDbTag = new DynamoDbTag();
        dynamoDbTag.pk = item.get(DynamoDb.Blog.PK).s();
        dynamoDbTag.sk = item.get(DynamoDb.Blog.SK).s();
        dynamoDbTag.gsi1Pk = item.get(DynamoDb.Blog.GSI1PK).s();
        dynamoDbTag.title = item.get(DynamoDb.Blog.title).s();
        dynamoDbTag.gsi1Sk = item.get(DynamoDb.Blog.GSI1SK).s();
        dynamoDbTag.readMinutes = Integer.parseInt(item.get(DynamoDb.Blog.readMinutes).n());
        return dynamoDbTag;
    }

    public Map<String, AttributeValue> to() {
        return Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s(pk).build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s(sk).build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n(String.valueOf(readMinutes)).build(),
                DynamoDb.Blog.title, AttributeValue.builder().s(title).build(),
                DynamoDb.Blog.GSI1PK, AttributeValue.builder().s(gsi1Pk).build(),
                DynamoDb.Blog.GSI1SK, AttributeValue.builder().s(gsi1Sk).build()
        );
    }

}
