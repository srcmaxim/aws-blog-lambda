package io.srcmaxim.blog.model.dynamodb;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbTagTest {

    @Test
    void from() {
        Map<String, AttributeValue> item = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("TAG#0").build(),
                DynamoDb.Blog.GSI1PK, AttributeValue.builder().s("TAG#DynamoDB").build(),
                DynamoDb.Blog.title, AttributeValue.builder().s("My DynamoDB Title").build(),
                DynamoDb.Blog.GSI1SK, AttributeValue.builder().s("PD#2020-08-20").build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n("1").build()
        );

        DynamoDbTag dynamoDbTag = DynamoDbTag.from(item);

        assertThat(dynamoDbTag.pk).isEqualTo("POST#build-dynamodb-blog");
        assertThat(dynamoDbTag.sk).isEqualTo("TAG#0");
        assertThat(dynamoDbTag.gsi1Pk).isEqualTo("TAG#DynamoDB");
        assertThat(dynamoDbTag.title).isEqualTo("My DynamoDB Title");
        assertThat(dynamoDbTag.gsi1Sk).isEqualTo("PD#2020-08-20");
        assertThat(dynamoDbTag.readMinutes).isEqualTo(1);
    }

    @Test
    void to() {
        DynamoDbTag dynamoDbTag = new DynamoDbTag();
        dynamoDbTag.pk = "POST#build-dynamodb-blog";
        dynamoDbTag.sk = "TAG#0";
        dynamoDbTag.gsi1Pk = "TAG#DynamoDB";
        dynamoDbTag.title = "My DynamoDB Title";
        dynamoDbTag.gsi1Sk = "PD#2020-08-20";
        dynamoDbTag.readMinutes = 1;

        Map<String, AttributeValue> item = dynamoDbTag.to();

        assertThat(item.get(DynamoDb.Blog.PK)).isEqualTo(AttributeValue.builder().s("POST#build-dynamodb-blog").build());
        assertThat(item.get(DynamoDb.Blog.SK)).isEqualTo(AttributeValue.builder().s("TAG#0").build());
        assertThat(item.get(DynamoDb.Blog.GSI1PK)).isEqualTo(AttributeValue.builder().s("TAG#DynamoDB").build());
        assertThat(item.get(DynamoDb.Blog.title)).isEqualTo(AttributeValue.builder().s("My DynamoDB Title").build());
        assertThat(item.get(DynamoDb.Blog.GSI1SK)).isEqualTo(AttributeValue.builder().s("PD#2020-08-20").build());
        assertThat(item.get(DynamoDb.Blog.readMinutes)).isEqualTo(AttributeValue.builder().n("1").build());
    }

}
