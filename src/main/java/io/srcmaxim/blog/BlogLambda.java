package io.srcmaxim.blog;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.srcmaxim.blog.config.MetaConfig;
import io.srcmaxim.blog.model.Post;
import io.srcmaxim.blog.model.PostMeta;
import io.srcmaxim.blog.service.BlogService;
import io.srcmaxim.blog.service.HealthService;
import io.srcmaxim.blog.service.ResponseService;
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
    ResponseService responseService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    HealthService healthService;

    @Inject
    MetaConfig metaConfig;

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent req, Context context) {
        try {
            return handleRequest(req);
        } catch (Exception e) {
            return responseService.handleError(e);
        }
    }

    private APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent req) throws Exception {
        String routeKey = req.getRouteKey();
        switch (routeKey) {
            case "GET /health": {
                Map<String, Object> health = healthService.getHealth();
                Object data = health.get("data");
                if (health.get("status").equals(200)) {
                    return responseService.ok(data);
                } else {
                    return responseService.error(data);
                }
            }
            case "GET /meta": {
                return responseService.ok(metaConfig.getMeta());
            }
            case "GET /error": {
                throw new RuntimeException("Error simulation");
            }
            case "GET /posts": {
                List<Post> posts = blogService.getPosts();
                return responseService.ok(posts);
            }
            case "GET /posts-meta": {
                String tag = Optional.ofNullable(req.getQueryStringParameters())
                        .map(pathParameters -> pathParameters.get("tag"))
                        .orElse("");
                List<PostMeta> postMetaByTag = blogService.getPostMetaByTag(tag);
                return responseService.ok(postMetaByTag);
            }
            case "GET /posts/{id}": {
                return Optional.ofNullable(req.getPathParameters())
                        .map(pathParameters -> pathParameters.get("id"))
                        .flatMap(blogService::getPost)
                        .map(responseService::ok)
                        .orElseGet(responseService::notFound);
            }
            case "DELETE /posts/{id}": {
                Optional.ofNullable(req.getPathParameters())
                        .map(pathParameters -> pathParameters.get("id"))
                        .flatMap(blogService::getPost)
                        .ifPresent(blogService::deletePost);
                return responseService.noContent();
            }
            case "PUT /posts/{id}":
            case "POST /posts": {
                Post post = objectMapper.readValue(req.getBody(), Post.class);
                post.id = Optional.ofNullable(req.getPathParameters())
                        .map(pathParameters -> pathParameters.get("id"))
                        .orElse(post.id);
                blogService.putPost(post);
                return responseService.noContentCreated(post.id);
            }
        }
        return null;
    }

}
