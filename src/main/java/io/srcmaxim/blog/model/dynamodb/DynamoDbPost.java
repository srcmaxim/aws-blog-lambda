package io.srcmaxim.blog.model.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.Map;

public class DynamoDbPost {

    public String pk;
    public String sk;
    public String content;
    public int readMinutes;
    public LocalDate publishDate;
    public String title;

    public static DynamoDbPost from(Map<String, AttributeValue> item) {
        var dynamoDbBlog = new DynamoDbPost();
        dynamoDbBlog.pk = item.get(DynamoDb.Blog.PK).s();
        dynamoDbBlog.sk = item.get(DynamoDb.Blog.SK).s();
        dynamoDbBlog.content = item.get(DynamoDb.Blog.content).s();
        dynamoDbBlog.readMinutes = Integer.parseInt(item.get(DynamoDb.Blog.readMinutes).n());
        dynamoDbBlog.publishDate = LocalDate.parse(item.get(DynamoDb.Blog.publishDate).s());
        dynamoDbBlog.title = item.get(DynamoDb.Blog.title).s();
        return dynamoDbBlog;
    }

    public Map<String, AttributeValue> to() {
        return Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s(pk).build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s(sk).build(),
                DynamoDb.Blog.content, AttributeValue.builder().s(content).build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n(String.valueOf(readMinutes)).build(),
                DynamoDb.Blog.publishDate, AttributeValue.builder().s(String.valueOf(publishDate)).build(),
                DynamoDb.Blog.title, AttributeValue.builder().s(title).build()
        );
    }

}
