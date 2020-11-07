package io.srcmaxim.blog.mapper;

import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;

import java.util.List;

public interface FromDynamoDbMapper {

    Post toPost(DynamoDbPost dynamoDbPost,
                List<DynamoDbTag> dynamoDbTags,
                List<DynamoDbMessage> dynamoDbMessages);

    PostMeta toPostMeta(DynamoDbPostMeta dynamoDbPostMeta);

}
