package io.srcmaxim.blog.parser;

import io.quarkus.test.junit.QuarkusTest;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.dynamodb.DynamoDb;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class DynamoDbParserTest {

    @Inject
    DynamoDbParser testee;

    @Test
    void parsePosts() {
        Map<String, AttributeValue> messageOneItem = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("MSG#2020-08-20").build(),
                DynamoDb.Blog.message, AttributeValue.builder().s("Cool post!!!").build(),
                DynamoDb.Blog.userEmail, AttributeValue.builder().s("aws-guru@gmail.com").build()
        );
        Map<String, AttributeValue> messageTwoItem = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("MSG#2020-08-21").build(),
                DynamoDb.Blog.message, AttributeValue.builder().s("Thanks!!!").build(),
                DynamoDb.Blog.userEmail, AttributeValue.builder().s("srcmaxim@gmail.com").build()
        );
        Map<String, AttributeValue> postItem = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("POST").build(),
                DynamoDb.Blog.content, AttributeValue.builder().s("https://{bucket}/build-dynamodb-blog").build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n("1").build(),
                DynamoDb.Blog.publishDate, AttributeValue.builder().s("2020-08-20").build(),
                DynamoDb.Blog.title, AttributeValue.builder().s("My DynamoDB Title").build()
        );
        Map<String, AttributeValue> tagOneItem = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("TAG#0").build(),
                DynamoDb.Blog.GSI1PK, AttributeValue.builder().s("TAG#DynamoDB").build(),
                DynamoDb.Blog.title, AttributeValue.builder().s("My DynamoDB Title").build(),
                DynamoDb.Blog.GSI1SK, AttributeValue.builder().s("PD#2020-08-20").build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n("1").build()
        );
        Map<String, AttributeValue> tagTwoItem = Map.of(
                DynamoDb.Blog.PK, AttributeValue.builder().s("POST#build-dynamodb-blog").build(),
                DynamoDb.Blog.SK, AttributeValue.builder().s("TAG#1").build(),
                DynamoDb.Blog.GSI1PK, AttributeValue.builder().s("TAG#Python").build(),
                DynamoDb.Blog.title, AttributeValue.builder().s("My DynamoDB Title").build(),
                DynamoDb.Blog.GSI1SK, AttributeValue.builder().s("PD#2020-08-20").build(),
                DynamoDb.Blog.readMinutes, AttributeValue.builder().n("1").build()
        );

        List<Map<String, AttributeValue>> items = List.of(
                messageOneItem,
                messageTwoItem,
                postItem,
                tagOneItem,
                tagTwoItem,

                messageOneItem,
                messageTwoItem,
                postItem,
                tagOneItem,
                tagTwoItem
        );

        List<Post> posts = testee.parsePosts(items);

        assertThat(posts).hasSize(2);

        Post post = posts.get(0);
        assertThat(post.id).isEqualTo("build-dynamodb-blog");
        assertThat(post.title).isEqualTo("My DynamoDB Title");
        assertThat(post.content).isEqualTo("https://{bucket}/build-dynamodb-blog");
        assertThat(post.readMinutes).isEqualTo(1);
        assertThat(post.publishDate).isEqualTo(LocalDate.parse("2020-08-20"));
        assertThat(post.tags).hasSize(2)
                .containsExactly("DynamoDB", "Python");
        assertThat(post.messages).hasSize(2)
                .extracting(this::extractMessage)
                .containsExactly(
                        Tuple.tuple(LocalDate.parse("2020-08-20"), "Cool post!!!", "aws-guru@gmail.com"),
                        Tuple.tuple(LocalDate.parse("2020-08-21"), "Thanks!!!", "srcmaxim@gmail.com")
                );

        post = posts.get(1);
        assertThat(post.id).isEqualTo("build-dynamodb-blog");
        assertThat(post.title).isEqualTo("My DynamoDB Title");
        assertThat(post.content).isEqualTo("https://{bucket}/build-dynamodb-blog");
        assertThat(post.readMinutes).isEqualTo(1);
        assertThat(post.publishDate).isEqualTo(LocalDate.parse("2020-08-20"));
        assertThat(post.tags).hasSize(2)
                .containsExactly("DynamoDB", "Python");
        assertThat(post.messages).hasSize(2)
                .extracting(this::extractMessage)
                .containsExactly(
                        Tuple.tuple(LocalDate.parse("2020-08-20"), "Cool post!!!", "aws-guru@gmail.com"),
                        Tuple.tuple(LocalDate.parse("2020-08-21"), "Thanks!!!", "srcmaxim@gmail.com")
                );
    }

    private Tuple extractMessage(Post.Message message) {
        return Tuple.tuple(message.createdAt, message.message, message.userEmail);
    }

}
