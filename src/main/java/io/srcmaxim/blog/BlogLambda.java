package io.srcmaxim.blog;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.service.BlogService;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Named("blog")
public class BlogLambda implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Logger LOG = Logger.getLogger(BlogLambda.class);

    @Inject
    BlogService blogService;

    @Inject
    MetaConfiguration metaConfiguration;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent req, Context context) {
        try {
            return handleRequest(req);
        } catch (Exception e) {
            return handleError(e);
        }
    }

    private APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent req) throws Exception {
        String routeKey = req.getRouteKey();
        switch (routeKey) {
            case "GET /health": {
                blogService.getPosts();
                return ok("ok");
            }
            case "GET /meta": {
                return ok(metaConfiguration.getMeta());
            }
            case "GET /posts": {
                List<Post> posts = blogService.getPosts();
                return ok(posts);
            }
            case "GET /posts-meta": {
                String tag = Optional.ofNullable(req.getQueryStringParameters())
                        .map(pathParameters -> pathParameters.get("tag"))
                        .orElse("");
                List<PostMeta> postMetaByTag = blogService.getPostMetaByTag(tag);
                return ok(postMetaByTag);
            }
            case "GET /posts/{id}": {
                return Optional.ofNullable(req.getPathParameters())
                        .map(pathParameters -> pathParameters.get("id"))
                        .flatMap(blogService::getPost)
                        .map(this::ok)
                        .orElseGet(this::notFound);
            }
            case "DELETE /posts/{id}": {
                Optional.ofNullable(req.getPathParameters())
                        .map(pathParameters -> pathParameters.get("id"))
                        .flatMap(blogService::getPost)
                        .ifPresent(blogService::deletePost);
                return noContent();
            }
            case "PUT /posts/{id}":
            case "POST /posts": {
                Post post = objectMapper.readValue(req.getBody(), Post.class);
                post.id = Optional.ofNullable(req.getPathParameters())
                        .map(pathParameters -> pathParameters.get("id"))
                        .orElse(post.id);
                blogService.putPost(post);
                return noContentCreated(post.id);
            }
        }
        return null;
    }

    private APIGatewayV2HTTPResponse ok(Object data) {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(200)
                .withBody(toJson(data))
                .build();
    }

    private APIGatewayV2HTTPResponse handleError(Exception exception) {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(500)
                .withBody(toJson(Map.of("error", exception.getMessage())))
                .build();
    }

    private APIGatewayV2HTTPResponse notFound() {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(404)
                .build();
    }

    private APIGatewayV2HTTPResponse noContent() {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(204)
                .build();
    }

    private APIGatewayV2HTTPResponse noContentCreated(String id) {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Location", "/posts/" + id
                ))
                .withStatusCode(204)
                .build();
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
