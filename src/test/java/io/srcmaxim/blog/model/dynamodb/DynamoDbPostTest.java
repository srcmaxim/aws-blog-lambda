package io.srcmaxim.blog.model.dynamodb;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbPostTest {

    @Test
    void from() {
        Map<String, AttributeValue> item = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("POST").build(),
                DynamoDb.Blog.content, AttributeValue.builder().s("https://{bucket}/build-dynamodb-blog").build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n("1").build(),
                DynamoDb.Blog.publishDate, AttributeValue.builder().s("2020-08-20").build(),
                DynamoDb.Blog.title, AttributeValue.builder().s("My DynamoDB Title").build()
        );

        DynamoDbPost dynamoDbPost = DynamoDbPost.from(item);

        assertThat(dynamoDbPost.pk).isEqualTo("POST#build-dynamodb-blog");
        assertThat(dynamoDbPost.sk).isEqualTo("POST");
        assertThat(dynamoDbPost.content).isEqualTo("https://{bucket}/build-dynamodb-blog");
        assertThat(dynamoDbPost.readMinutes).isEqualTo(1);
        assertThat(dynamoDbPost.publishDate).isEqualTo(LocalDate.parse("2020-08-20"));
        assertThat(dynamoDbPost.title).isEqualTo("My DynamoDB Title");
    }

    @Test
    void to() {
        DynamoDbPost dynamoDbPost = new DynamoDbPost();
        dynamoDbPost.pk = "POST#build-dynamodb-blog";
        dynamoDbPost.sk = "POST";
        dynamoDbPost.content = "https://{bucket}/build-dynamodb-blog";
        dynamoDbPost.readMinutes = 1;
        dynamoDbPost.publishDate = LocalDate.parse("2020-08-20");
        dynamoDbPost.title = "My DynamoDB Title";

        Map<String, AttributeValue> item = dynamoDbPost.to();

        assertThat(item.get(DynamoDb.Blog.PK)).isEqualTo(AttributeValue.builder().s("POST#build-dynamodb-blog").build());
        assertThat(item.get(DynamoDb.Blog.SK)).isEqualTo(AttributeValue.builder().s("POST").build());
        assertThat(item.get(DynamoDb.Blog.content)).isEqualTo(AttributeValue.builder().s("https://{bucket}/build-dynamodb-blog").build());
        assertThat(item.get(DynamoDb.Blog.readMinutes)).isEqualTo(AttributeValue.builder().n("1").build());
        assertThat(item.get(DynamoDb.Blog.publishDate)).isEqualTo(AttributeValue.builder().s("2020-08-20").build());
        assertThat(item.get(DynamoDb.Blog.title)).isEqualTo(AttributeValue.builder().s("My DynamoDB Title").build());
    }

}
