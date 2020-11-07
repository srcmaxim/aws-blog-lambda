package io.srcmaxim.blog.mapper;

import io.quarkus.test.junit.QuarkusTest;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class FromDynamoDbMapperTest {

    @Inject
    FromDynamoDbMapper testee;

    @Test
    void toPost() {
        DynamoDbPost dynamoDbPost = new DynamoDbPost();
        dynamoDbPost.pk = "POST#build-dynamodb-blog";
        dynamoDbPost.title = "My DynamoDB Title";
        dynamoDbPost.content = "https://{bucket}/build-dynamodb-blog";
        dynamoDbPost.readMinutes = 1;
        dynamoDbPost.publishDate = LocalDate.parse("2020-10-20");
        DynamoDbTag dynamoDbTag1 = new DynamoDbTag();
        dynamoDbTag1.gsi1Pk = "TAG#DynamoDB";
        DynamoDbTag dynamoDbTag2 = new DynamoDbTag();
        dynamoDbTag2.gsi1Pk = "TAG#Python";
        List<DynamoDbTag> dynamoDbTags = List.of(dynamoDbTag1, dynamoDbTag2);
        DynamoDbMessage dynamoDbMessage1 = new DynamoDbMessage();
        dynamoDbMessage1.sk = "MSG#2020-10-10";
        dynamoDbMessage1.message = "Hi one";
        dynamoDbMessage1.userEmail = "one@email.com";
        DynamoDbMessage dynamoDbMessage2 = new DynamoDbMessage();
        dynamoDbMessage2.sk = "MSG#2020-10-20";
        dynamoDbMessage2.message = "Hi two";
        dynamoDbMessage2.userEmail = "two@email.com";
        List<DynamoDbMessage> dynamoDbMessages = List.of(dynamoDbMessage1, dynamoDbMessage2);

        Post post = testee.toPost(dynamoDbPost, dynamoDbTags, dynamoDbMessages);

        assertThat(post.id).isEqualTo("build-dynamodb-blog");
        assertThat(post.title).isEqualTo("My DynamoDB Title");
        assertThat(post.content).isEqualTo("https://{bucket}/build-dynamodb-blog");
        assertThat(post.readMinutes).isEqualTo(1);
        assertThat(post.publishDate).isEqualTo(LocalDate.parse("2020-10-20"));
        assertThat(post.tags).hasSize(2)
                .containsExactly("DynamoDB", "Python");
        assertThat(post.messages).hasSize(2)
                .extracting(this::extractMessage)
                .containsExactly(
                        Tuple.tuple(LocalDate.parse("2020-10-10"), "Hi one", "one@email.com"),
                        Tuple.tuple(LocalDate.parse("2020-10-20"), "Hi two", "two@email.com")
                );
    }

    private Tuple extractMessage(Post.Message message) {
        return Tuple.tuple(message.createdAt, message.message, message.userEmail);
    }

    @Test
    void toPostMeta() {
        DynamoDbPostMeta dynamoDbPostMeta = new DynamoDbPostMeta();
        dynamoDbPostMeta.pk = "POST#build-dynamodb-blog";
        dynamoDbPostMeta.sk = "TAG#0";
        dynamoDbPostMeta.gsi1Pk = "TAG#DynamoDB";
        dynamoDbPostMeta.gsi1Sk = "PD#2020-08-20";
        dynamoDbPostMeta.readMinutes = 1;
        dynamoDbPostMeta.title = "My DynamoDB Title";

        PostMeta postMeta = testee.toPostMeta(dynamoDbPostMeta);

        assertThat(postMeta.id).isEqualTo("build-dynamodb-blog");
        assertThat(postMeta.title).isEqualTo("My DynamoDB Title");
        assertThat(postMeta.readMinutes).isEqualTo(1);
        assertThat(postMeta.publishDate).isEqualTo(LocalDate.parse("2020-08-20"));
        assertThat(postMeta.tag).isEqualTo("DynamoDB");
    }

}
