package io.srcmaxim.blog.mapper;

import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPostMeta;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "cdi")
interface FromDynamoDbMapperMapstruct extends FromDynamoDbMapper {

    default Post toPost(DynamoDbPost dynamoDbPost,
                        List<DynamoDbTag> dynamoDbTags,
                        List<DynamoDbMessage> dynamoDbMessages) {
        Post post = toPost(dynamoDbPost);
        post.tags = toTags(dynamoDbTags);
        post.messages = toMessages(dynamoDbMessages);
        return post;
    }

    @Mapping(source = "pk", target = "id", qualifiedByName = "mapId")
    @Mapping(source = "gsi1Pk", target = "tag", qualifiedByName = "mapTag")
    @Mapping(source = "gsi1Sk", target = "publishDate", qualifiedByName = "mapPublishDate")
    PostMeta toPostMeta(DynamoDbPostMeta dynamoDbPostMeta);

    @Mapping(source = "pk", target = "id", qualifiedByName = "mapId")
    Post toPost(DynamoDbPost dynamoDbPost);

    @Named("mapId")
    default String mapId(String pk) {
        return pk.substring(5);
    }

    List<String> toTags(List<DynamoDbTag> dynamoDbTags);

    default String toTag(DynamoDbTag dynamoDbTags) {
        return dynamoDbTags.gsi1Pk.substring(4);
    }

    List<Post.Message> toMessages(List<DynamoDbMessage> dynamoDbMessages);

    @Mapping(source = "sk", target = "createdAt", qualifiedByName = "mapCreatedAt")
    Post.Message toMessage(DynamoDbMessage dynamoDbMessage);

    @Named("mapCreatedAt")
    default LocalDate mapCreatedAt(String sk) {
        return LocalDate.parse(sk.substring(4));
    }

    @Named("mapTag")
    default String mapTag(String gsi1Pk) {
        return gsi1Pk.substring(4);
    }

    @Named("mapPublishDate")
    default LocalDate mapPublishDate(String gsi1Sk) {
        return LocalDate.parse(gsi1Sk.substring(3));
    }

}
