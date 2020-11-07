package io.srcmaxim.blog.model.dynamodb;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbMessageTest {

    @Test
    void from() {
        Map<String, AttributeValue> item = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("MSG#2020-08-21").build(),
                DynamoDb.Blog.message, AttributeValue.builder().s("Cool post!!!").build(),
                DynamoDb.Blog.userEmail, AttributeValue.builder().s("aws-guru@gmail.com").build()
        );

        DynamoDbMessage dynamoDbMessage = DynamoDbMessage.from(item);

        assertThat(dynamoDbMessage.pk).isEqualTo("POST#build-dynamodb-blog");
        assertThat(dynamoDbMessage.sk).isEqualTo("MSG#2020-08-21");
        assertThat(dynamoDbMessage.message).isEqualTo("Cool post!!!");
        assertThat(dynamoDbMessage.userEmail).isEqualTo("aws-guru@gmail.com");
    }

    @Test
    void to() {
        DynamoDbMessage dynamoDbMessage = new DynamoDbMessage();
        dynamoDbMessage.pk = "POST#build-dynamodb-blog";
        dynamoDbMessage.sk = "MSG#2020-08-21";
        dynamoDbMessage.message = "Cool post!!!";
        dynamoDbMessage.userEmail = "aws-guru@gmail.com";

        Map<String, AttributeValue> item = dynamoDbMessage.to();

        assertThat(item.get(DynamoDb.Blog.PK)).isEqualTo(AttributeValue.builder().s("POST#build-dynamodb-blog").build());
        assertThat(item.get(DynamoDb.Blog.SK)).isEqualTo(AttributeValue.builder().s("MSG#2020-08-21").build());
        assertThat(item.get(DynamoDb.Blog.message)).isEqualTo(AttributeValue.builder().s("Cool post!!!").build());
        assertThat(item.get(DynamoDb.Blog.userEmail)).isEqualTo(AttributeValue.builder().s("aws-guru@gmail.com").build());
    }

}
