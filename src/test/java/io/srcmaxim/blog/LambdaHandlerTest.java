package io.srcmaxim.blog;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.repository.CreateDynamoDbTable;
import io.srcmaxim.blog.repository.DynamoDbLocalResource;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(DynamoDbLocalResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LambdaHandlerTest {

    private static final Logger LOG = Logger.getLogger(BlogLambda.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    CreateDynamoDbTable createDynamoDbTable;

    @Order(100)
    @ParameterizedTest
    @MethodSource("providePosts")
    void handleRequest_WhenPostPosts_ThenReturnCreated(Post post) {
        String postId = post.id;
        post.id = null;
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("POST /posts");
        in.setBody(toJson(post));
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(out.getHeaders()).containsEntry("Location", "/posts/" + postId);
    }

    @Test
    @Order(200)
    public void handleRequest_WhenPutPosts_ThenReturnCreated() {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("PUT /posts/{id}");
        Post post = postOne();
        in.setPathParameters(Map.of("id", post.id));
        in.setBody(toJson(post));
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(204);
        assertThat(out.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(out.getHeaders()).containsEntry("Location", "/posts/" + post.id);
    }

    @Test
    @Order(300)
    public void handleRequest_WhenGetPostsId_ThenReturnPosts() {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("GET /posts/{id}");
        in.setPathParameters(Map.of("id", postOne().id));
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(200);
        assertThat(out.getHeaders()).containsEntry("Content-Type", "application/json");
        String body = out.getBody();
        assertThat(body).isNotNull();
        assertThatJson(body)
                .isEqualTo(toJson(postOne()));
    }

    @Test
    @Order(350)
    public void handleRequest_WhenGetPostsTag_ThenReturnPostMetas() {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("GET /posts-meta");
        in.setQueryStringParameters(Map.of("tag", "Python"));
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(200);
        assertThat(out.getHeaders()).containsEntry("Content-Type", "application/json");
        String body = out.getBody();
        assertThat(body).isNotNull();
        assertThatJson(body)
                .isEqualTo(toJson(postMetas()));
    }

    @Test
    @Order(400)
    public void handleRequest_WhenGetPosts_ThenReturnPosts() {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("GET /posts");
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(200);
        assertThat(out.getHeaders()).containsEntry("Content-Type", "application/json");
        String body = out.getBody();
        assertThat(body).isNotNull();
        assertThatJson(body)
                .isEqualTo(toJson(List.of(postOne(), postTwo())));
    }

    @Order(600)
    @ParameterizedTest
    @CsvSource({"my-dynamodb-title", "my-quarkus-python-title"})
    public void handleRequest_WhenDeletePosts_ThenReturnNoContent(String id) {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("DELETE /posts/{id}");
        in.setPathParameters(Map.of("id", id));
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(204);
    }

    @Test
    @Order(700)
    public void handleRequest_WhenDeletePostsAndPostNotExists_ThenReturnNoContent() {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("DELETE /posts/{id}");
        in.setPathParameters(Map.of("id", "my-dynamodb-title"));
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(204);
    }

    @Test
    @Order(800)
    public void handleRequest_WhenGetPostsAndPostsNotExists_ThenReturnPosts() {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("GET /posts");
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(200);
        assertThat(out.getHeaders()).containsEntry("Content-Type", "application/json");
        String body = out.getBody();
        assertThat(body).isNotNull();
        assertThatJson(body)
                .isEqualTo("[]");
    }

    @Test
    @Order(900)
    public void handleRequest_WhenGetPostsIdAndPostsNotExists_ThenReturnPosts() {
        APIGatewayV2HTTPEvent in = new APIGatewayV2HTTPEvent();
        in.setRouteKey("GET /posts/{id}");
        in.setPathParameters(Map.of("id", "my-dynamodb-title"));
        APIGatewayV2HTTPResponse out = LambdaClient.invoke(APIGatewayV2HTTPResponse.class, in);
        assertThat(out.getStatusCode()).isEqualTo(404);
    }

    private static Stream<Arguments> providePosts() {
        return Stream.of(
                Arguments.of(postOne()),
                Arguments.of(postTwo())
        );
    }

    private static Post postOne() {
        Post post = new Post();
        post.id = "my-dynamodb-title";
        post.title = "My DynamoDB Title";
        post.content = "https://{bucket}/build-dynamodb-blog";
        post.readMinutes = 1;
        post.publishDate = LocalDate.parse("2020-08-20");
        post.tags = List.of("DynamoDB", "Python");
        Post.Message messageOne = new Post.Message();
        messageOne.createdAt = LocalDate.parse("2020-08-20");
        messageOne.message = "Cool post!!!";
        messageOne.userEmail = "aws-guru@gmail.com";
        Post.Message messageTwo = new Post.Message();
        messageTwo.createdAt = LocalDate.parse("2020-08-21");
        messageTwo.message = "Thanks!!!";
        messageTwo.userEmail = "srcmaxim@gmail.com";
        post.messages = List.of(messageOne, messageTwo);
        return post;
    }

    private static Post postTwo() {
        Post post = new Post();
        post.id = "my-quarkus-python-title";
        post.title = "My Quarkus Python Title";
        post.content = "https://{bucket}/{graalvm-python}";
        post.readMinutes = 3;
        post.publishDate = LocalDate.parse("2020-08-18");
        post.tags = List.of("GraalVm", "Python");
        Post.Message messageOne = new Post.Message();
        messageOne.createdAt = LocalDate.parse("2020-09-16");
        messageOne.message = "Wow, python running in Java";
        messageOne.userEmail = "some-guy@gmail.com";
        Post.Message messageTwo = new Post.Message();
        messageTwo.createdAt = LocalDate.parse("2020-09-20");
        messageTwo.message = "It's also integrated in Quarkus";
        messageTwo.userEmail = "srcmaxim@gmail.com";
        post.messages = List.of(messageOne, messageTwo);
        return post;
    }

    private static List<PostMeta> postMetas() {
        PostMeta postMetaOne = new PostMeta();
        postMetaOne.id = "my-dynamodb-title";
        postMetaOne.title = "My DynamoDB Title";
        postMetaOne.readMinutes = 1;
        postMetaOne.publishDate = LocalDate.parse("2020-08-20");
        postMetaOne.tag = "Python";

        PostMeta postMetaTwo = new PostMeta();
        postMetaTwo.id = "my-quarkus-python-title";
        postMetaTwo.title = "My Quarkus Python Title";
        postMetaTwo.readMinutes = 3;
        postMetaTwo.publishDate = LocalDate.parse("2020-08-18");
        postMetaTwo.tag = "Python";

        return List.of(postMetaOne, postMetaTwo);
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

}
