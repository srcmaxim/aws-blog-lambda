package io.srcmaxim.blog.mapper;

import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;

import java.util.List;

public interface ToDynamoDbMapper {

    DynamoDbPost toDynamoDbBlog(Post post);

    List<DynamoDbTag> toDynamoDbTags(Post post);

    List<DynamoDbMessage> toDynamoDbMessages(Post post);

}
