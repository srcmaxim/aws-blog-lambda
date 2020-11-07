package io.srcmaxim.blog.parser;

import io.srcmaxim.blog.mapper.FromDynamoDbMapper;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.dynamodb.DynamoDb;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DynamoDbParser {

    @Inject
    FromDynamoDbMapper fromDynamoDbMapper;

    public List<Post> parsePosts(List<Map<String, AttributeValue>> items) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < items.size();) {
            Pair<DynamoDbMessage> dynamoDbMessages = parseType(items, i, DynamoDbMessage.class);
            i = dynamoDbMessages.index;
            Pair<DynamoDbPost> dynamoDbPosts = parseType(items, i, DynamoDbPost.class);
            i = dynamoDbPosts.index;
            Pair<DynamoDbTag> dynamoDbTags = parseType(items, i, DynamoDbTag.class);
            i = dynamoDbTags.index;
            if (dynamoDbPosts.list.size() == 1) {
                posts.add(fromDynamoDbMapper.toPost(dynamoDbPosts.list.get(0), dynamoDbTags.list, dynamoDbMessages.list));
            }
        }
        return posts;
    }

    private <T> Pair<T> parseType(List<Map<String, AttributeValue>> items, int i, Class<T> classType) {
        List<T> list = new ArrayList<>();
        for (; i < items.size(); i++) {
            Object o = mapItem(items.get(i));
            if (classType.isInstance(o)) {
                list.add(classType.cast(o));
            } else {
                break;
            }
        }
        Pair<T> pair = new Pair<>();
        pair.index = i;
        pair.list = list;
        return pair;
    }

    private Object mapItem(Map<String, AttributeValue> item) {
        String type = item.get(DynamoDb.Blog.SK).s();
        if (type.equals("POST")) {
            return DynamoDbPost.from(item);
        } else if (type.startsWith("TAG#")) {
            return DynamoDbTag.from(item);
        } else if (type.startsWith("MSG#")) {
            return DynamoDbMessage.from(item);
        } else {
            throw new RuntimeException("dynamodb_exception");
        }
    }

    private static class Pair<T> {
        private int index;
        private List<T> list;
    }

}
