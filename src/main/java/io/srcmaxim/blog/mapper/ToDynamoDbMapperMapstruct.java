package io.srcmaxim.blog.mapper;

import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.dynamodb.DynamoDbMessage;
import io.srcmaxim.blog.model.dynamodb.DynamoDbPost;
import io.srcmaxim.blog.model.dynamodb.DynamoDbTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "cdi")
public interface ToDynamoDbMapperMapstruct extends ToDynamoDbMapper {

    @Mapping(source = "id", target = "pk", qualifiedByName = "mapPk")
    @Mapping(source = "id", target = "sk", qualifiedByName = "mapSkPost")
    DynamoDbPost toDynamoDbBlog(Post post);

    default List<DynamoDbTag> toDynamoDbTags(Post post) {
        List<DynamoDbTag> list = new ArrayList(post.tags.size());
        for (int i = 0; i < post.tags.size(); i++) {
            DynamoDbTag dynamoDbTag = toDynamoDbTag(post, i, post.tags.get(i));
            list.add(dynamoDbTag);
        }
        return list;
    }

    @Mapping(source = "post.id", target = "pk", qualifiedByName = "mapPk")
    @Mapping(source = "id", target = "sk", qualifiedByName = "mapSkTag")
    @Mapping(source = "tag", target = "gsi1Pk", qualifiedByName = "mapGsi1Pk")
    @Mapping(source = "post.publishDate", target = "gsi1Sk", qualifiedByName = "mapGsi1Sk")
    DynamoDbTag toDynamoDbTag(Post post, int id, String tag);

    default List<DynamoDbMessage> toDynamoDbMessages(Post post) {
        List<DynamoDbMessage> list = new ArrayList(post.messages.size());
        for (int i = 0; i < post.messages.size(); i++) {
            DynamoDbMessage dynamoDbMessage = toDynamoDbMessage(post, post.messages.get(i));
            list.add(dynamoDbMessage);
        }
        return list;
    }

    @Mapping(source = "post.id", target = "pk", qualifiedByName = "mapPk")
    @Mapping(source = "message.createdAt", target = "sk", qualifiedByName = "mapSkMessage")
    @Mapping(source = "message.message", target = "message")
    @Mapping(source = "message.userEmail", target = "userEmail")
    DynamoDbMessage toDynamoDbMessage(Post post, Post.Message message);

    @Named("mapPk")
    default String mapPk(String id) {
        return "POST#" + id;
    }

    @Named("mapSkPost")
    default String mapSkPost(String ignored) {
        return "POST";
    }

    @Named("mapSkTag")
    default String mapSkTag(int id) {
        return "TAG#" + id;
    }

    @Named("mapGsi1Pk")
    default String mapGsi1Pk(String tag) {
        return "TAG#" + tag;
    }

    @Named("mapGsi1Sk")
    default String mapGsi1Sk(LocalDate publishDate) {
        return "PD#" + publishDate.toString();
    }

    @Named("mapSkMessage")
    default String mapSkMessage(LocalDate createdAt) {
        return "MSG#" + createdAt;
    }

}
