package io.srcmaxim.blog.model.dynamodb;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbPostMetaTest {

    @Test
    void from() {
        Map<String, AttributeValue> item = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("POST").build(),
                DynamoDb.Blog.GSI1PK, AttributeValue.builder().s("TAG#DynamoDB").build(),
                DynamoDb.Blog.GSI1SK, AttributeValue.builder().s("PD#2020-08-20").build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n("1").build(),
                DynamoDb.Blog.title, AttributeValue.builder().s("My DynamoDB Title").build()
        );

        DynamoDbPostMeta dynamoDbPostMeta = DynamoDbPostMeta.from(item);

        assertThat(dynamoDbPostMeta.pk).isEqualTo("POST#build-dynamodb-blog");
        assertThat(dynamoDbPostMeta.sk).isEqualTo("POST");
        assertThat(dynamoDbPostMeta.gsi1Pk).isEqualTo("TAG#DynamoDB");
        assertThat(dynamoDbPostMeta.gsi1Sk).isEqualTo("PD#2020-08-20");
        assertThat(dynamoDbPostMeta.readMinutes).isEqualTo(1);
        assertThat(dynamoDbPostMeta.title).isEqualTo("My DynamoDB Title");
    }

}
