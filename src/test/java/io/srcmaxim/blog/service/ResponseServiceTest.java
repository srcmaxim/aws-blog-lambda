package io.srcmaxim.blog.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ResponseServiceTest {

    @Inject
    ResponseService testee;

    @Test
    void ok() {
        APIGatewayV2HTTPResponse actual = testee.ok(List.of());

        assertThat(actual.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(actual.getStatusCode()).isEqualTo(200);
        assertThat(actual.getBody()).isEqualTo("[]");
    }

    @Test
    void handleError() {
        APIGatewayV2HTTPResponse actual = testee.handleError(new RuntimeException("message"));

        assertThat(actual.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(actual.getStatusCode()).isEqualTo(500);
        assertThat(actual.getBody()).isEqualTo("{\"error\":\"message\"}");
    }

    @Test
    void notFound() {
        APIGatewayV2HTTPResponse actual = testee.notFound();

        assertThat(actual.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(actual.getStatusCode()).isEqualTo(404);
    }

    @Test
    void noContent() {
        APIGatewayV2HTTPResponse actual = testee.noContent();

        assertThat(actual.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(actual.getStatusCode()).isEqualTo(204);
    }

    @Test
    void noContentCreated() {
        APIGatewayV2HTTPResponse actual = testee.noContentCreated("ID");

        assertThat(actual.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(actual.getHeaders()).containsEntry("Location", "/posts/ID");
        assertThat(actual.getStatusCode()).isEqualTo(204);
    }

    @Test
    void error() {
        APIGatewayV2HTTPResponse actual = testee.error(List.of());

        assertThat(actual.getHeaders()).containsEntry("Content-Type", "application/json");
        assertThat(actual.getStatusCode()).isEqualTo(500);
        assertThat(actual.getBody()).isEqualTo("[]");
    }

}
