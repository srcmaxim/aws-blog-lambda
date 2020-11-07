package io.srcmaxim.blog.mapper;

import io.quarkus.test.junit.QuarkusTest;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ToDynamoDbMapperTest {

    @Inject
    ToDynamoDbMapper testee;

    @Test
    void toDynamoDbPost() {
        Post post = new Post();
        post.id = "build-dynamodb-blog";
        post.title = "My DynamoDB Title";
        post.content = "https://{bucket}/build-dynamodb-blog";
        post.readMinutes = 1;
        post.publishDate = LocalDate.parse("2020-10-20");
        post.tags = List.of("DynamoDB", "Python");
        Post.Message message1 = new Post.Message();
        message1.createdAt = LocalDate.parse("2020-10-10");
        message1.message = "Hi one";
        message1.userEmail = "one@email.com";
        Post.Message message2 = new Post.Message();
        message2.createdAt = LocalDate.parse("2020-10-20");
        message2.message = "Hi two";
        message2.userEmail = "two@email.com";
        post.messages = List.of(message1, message2);

        DynamoDbPost dynamoDbPost = testee.toDynamoDbBlog(post);

        assertThat(dynamoDbPost.pk).isEqualTo("POST#build-dynamodb-blog");
        assertThat(dynamoDbPost.sk).isEqualTo("POST");
        assertThat(dynamoDbPost.content).isEqualTo("https://{bucket}/build-dynamodb-blog");
        assertThat(dynamoDbPost.readMinutes).isEqualTo(1);
        assertThat(dynamoDbPost.publishDate).isEqualTo(LocalDate.parse("2020-10-20"));
        assertThat(dynamoDbPost.title).isEqualTo("My DynamoDB Title");
    }

    @Test
    void toDynamoDbTags() {
        Post post = new Post();
        post.id = "build-dynamodb-blog";
        post.title = "My DynamoDB Title";
        post.content = "https://{bucket}/build-dynamodb-blog";
        post.readMinutes = 1;
        post.publishDate = LocalDate.parse("2020-10-20");
        post.tags = List.of("DynamoDB", "Python");
        Post.Message message1 = new Post.Message();
        message1.createdAt = LocalDate.parse("2020-10-10");
        message1.message = "Hi one";
        message1.userEmail = "one@email.com";
        Post.Message message2 = new Post.Message();
        message2.createdAt = LocalDate.parse("2020-10-20");
        message2.message = "Hi two";
        message2.userEmail = "two@email.com";
        post.messages = List.of(message1, message2);

        List<DynamoDbTag> dynamoDbTags = testee.toDynamoDbTags(post);

        assertThat(dynamoDbTags).hasSize(2)
                .extracting(this::extractMessage)
                .containsExactly(
                        Tuple.tuple("POST#build-dynamodb-blog", "TAG#0", 1, "TAG#DynamoDB", "PD#2020-10-20", "My DynamoDB Title"),
                        Tuple.tuple("POST#build-dynamodb-blog", "TAG#1", 1, "TAG#Python", "PD#2020-10-20", "My DynamoDB Title")
                );
    }

    private Tuple extractMessage(DynamoDbTag dynamoDbTag) {
        return Tuple.tuple(dynamoDbTag.pk, dynamoDbTag.sk, dynamoDbTag.readMinutes,
                dynamoDbTag.gsi1Pk, dynamoDbTag.gsi1Sk, dynamoDbTag.title);
    }

    @Test
    void toDynamoDbMessages() {
        Post post = new Post();
        post.id = "build-dynamodb-blog";
        post.title = "My DynamoDB Title";
        post.content = "https://{bucket}/build-dynamodb-blog";
        post.readMinutes = 1;
        post.publishDate = LocalDate.parse("2020-10-20");
        post.tags = List.of("DynamoDB", "Python");
        Post.Message message1 = new Post.Message();
        message1.createdAt = LocalDate.parse("2020-10-10");
        message1.message = "Hi one";
        message1.userEmail = "one@email.com";
        Post.Message message2 = new Post.Message();
        message2.createdAt = LocalDate.parse("2020-10-20");
        message2.message = "Hi two";
        message2.userEmail = "two@email.com";
        post.messages = List.of(message1, message2);
        post.messages = List.of(message1, message2);

        List<DynamoDbMessage> dynamoDbMessages = testee.toDynamoDbMessages(post);

        assertThat(dynamoDbMessages).hasSize(2)
                .extracting(this::extractMessage)
                .containsExactly(
                        Tuple.tuple("POST#build-dynamodb-blog", "MSG#2020-10-10", "Hi one", "one@email.com"),
                        Tuple.tuple("POST#build-dynamodb-blog", "MSG#2020-10-20", "Hi two", "two@email.com")
                );
    }

    private Tuple extractMessage(DynamoDbMessage dynamoDbMessage) {
        return Tuple.tuple(dynamoDbMessage.pk, dynamoDbMessage.sk, dynamoDbMessage.message, dynamoDbMessage.userEmail);
    }

}
