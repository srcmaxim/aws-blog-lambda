package io.srcmaxim.blog.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class ResponseService {

    private static final Logger LOG = Logger.getLogger(ResponseService.class);

    @Inject
    ObjectMapper objectMapper;

    public APIGatewayV2HTTPResponse ok(Object data) {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(200)
                .withBody(toJson(data))
                .build();
    }

    public APIGatewayV2HTTPResponse handleError(Exception exception) {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(500)
                .withBody(toJson(Map.of("error", exception.getMessage())))
                .build();
    }

    public APIGatewayV2HTTPResponse notFound() {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(404)
                .build();
    }

    public APIGatewayV2HTTPResponse noContent() {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(204)
                .build();
    }

    public APIGatewayV2HTTPResponse noContentCreated(String id) {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of(
                        "Content-Type", "application/json",
                        "Location", "/posts/" + id
                ))
                .withStatusCode(204)
                .build();
    }

    public APIGatewayV2HTTPResponse error(Object data) {
        return APIGatewayV2HTTPResponse.builder()
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withStatusCode(500)
                .withBody(toJson(data))
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
