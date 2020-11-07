package io.srcmaxim.blog.repository;

import io.srcmaxim.blog.mapper.FromDynamoDbMapper;
import io.srcmaxim.blog.mapper.ToDynamoDbMapper;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;
import io.srcmaxim.blog.parser.DynamoDbParser;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class DynamoDbRepository extends AbstractDynamoDbRepository {

    @Inject
    ToDynamoDbMapper toDynamoDbMapper;
    @Inject
    FromDynamoDbMapper fromDynamoDbMapper;
    @Inject
    DynamoDbParser dynamoDbParser;
    @Inject
    DynamoDbClient dynamoDB;

    public void putPost(Post post) {
        putDynamoDbPost(toDynamoDbMapper.toDynamoDbBlog(post));
        putDynamoDbTags(toDynamoDbMapper.toDynamoDbTags(post));
        putDynamoDbMessages(toDynamoDbMapper.toDynamoDbMessages(post));
    }

    private void putDynamoDbPost(DynamoDbPost dynamoDbPost) {
        try {
            PutItemRequest putItemRequest = putRequest(dynamoDbPost::to);
            dynamoDB.putItem(putItemRequest);
        } catch (Exception e) {
            handleCommonErrors(e);
            throw new RuntimeException("dynamodb_exception");
        }
    }

    private void putDynamoDbTags(List<DynamoDbTag> dynamoDbTags) {
        try {
            for (DynamoDbTag dynamoDbTag : dynamoDbTags) {
                PutItemRequest putItemRequest = putRequest(dynamoDbTag::to);
                dynamoDB.putItem(putItemRequest);
            }
        } catch (Exception e) {
            handleCommonErrors(e);
            throw new RuntimeException("dynamodb_exception");
        }
    }

    private void putDynamoDbMessages(List<DynamoDbMessage> dynamoDbMessages) {
        try {
            for (DynamoDbMessage dynamoDbMessage : dynamoDbMessages) {
                PutItemRequest putItemRequest = putRequest(dynamoDbMessage::to);
                dynamoDB.putItem(putItemRequest);
            }
        } catch (Exception e) {
            handleCommonErrors(e);
            throw new RuntimeException("dynamodb_exception");
        }
    }

    public Optional<Post> getPost(String id) {
        try {
            QueryResponse queryResponse = dynamoDB.query(queryRequest(id));
            List<Map<String, AttributeValue>> items = queryResponse.items();
            List<Post> posts = dynamoDbParser.parsePosts(items);
            return posts.size() == 1 ? Optional.of(posts.get(0)) : Optional.empty();
        } catch (Exception e) {
            handleCommonErrors(e);
            throw new RuntimeException("dynamodb_exception");
        }
    }

    public List<Post> getPosts() {
        try {
            dynamoDB.scan(ScanRequest.builder()
                    .tableName("Blog")
                    .consistentRead(false)
                    .build());
            ScanRequest scanRequest = scanRequest();
            ScanResponse scanResponse = dynamoDB.scan(scanRequest);
            List<Map<String, AttributeValue>> items = scanResponse.items();
            List<Post> posts = dynamoDbParser.parsePosts(items);
            return posts;
        } catch (Exception e) {
            handleCommonErrors(e);
            throw new RuntimeException("dynamodb_exception");
        }
    }

    public List<PostMeta> getPostMetasByTag(String tag) {
        try {
            QueryRequest queryRequest = queryByTagRequest(tag);
            QueryResponse queryResponse = dynamoDB.query(queryRequest);
            List<PostMeta> postMetas = queryResponse.items().stream()
                    .map(DynamoDbPostMeta::from)
                    .map(fromDynamoDbMapper::toPostMeta)
                    .collect(Collectors.toList());
            return postMetas;
        } catch (Exception e) {
            handleCommonErrors(e);
            throw new RuntimeException("dynamodb_exception");
        }
    }

    public void deletePost(Post post) {
        try {
            List<DeleteItemRequest> deleteRequests = deleteRequest(post);
            for (DeleteItemRequest deleteRequest : deleteRequests) {
                dynamoDB.deleteItem(deleteRequest);
            }
        } catch (Exception e) {
            handleCommonErrors(e);
            throw new RuntimeException("dynamodb_exception");
        }
    }

}
