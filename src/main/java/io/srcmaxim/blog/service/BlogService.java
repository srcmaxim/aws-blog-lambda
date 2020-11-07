package io.srcmaxim.blog.service;

import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.repository.DynamoDbRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BlogService {

    @Inject
    DynamoDbRepository dynamoDbRepository;

    public void putPost(Post post) {
        dynamoDbRepository.putPost(post);
    }

    public List<Post> getPosts() {
        return dynamoDbRepository.getPosts();
    }

    public Optional<Post> getPost(String id) {
        return dynamoDbRepository.getPost(id);
    }

    public void deletePost(Post post) {
        dynamoDbRepository.deletePost(post);
    }

    public List<PostMeta> getPostMetaByTag(String tag) {
        return dynamoDbRepository.getPostMetasByTag(tag);
    }

}
