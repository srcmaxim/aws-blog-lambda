package io.srcmaxim.blog.repository;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.function.Consumer;

public class DynamoDbLocalResource implements QuarkusTestResourceLifecycleManager {

    public static final StringBuffer LOGGER = new StringBuffer();

    private GenericContainer dynamodbLocal = new GenericContainer(DockerImageName.parse("amazon/dynamodb-local:1.11.477"))
            .withExposedPorts(8000)
            .withCommand("-jar DynamoDBLocal.jar -inMemory -sharedDb")
            .withLogConsumer((Consumer<OutputFrame>) this::staticLogConsumer);

    @Override
    public Map<String, String> start() {
        dynamodbLocal.start();
        String host = dynamodbLocal.getHost();
        Integer port = dynamodbLocal.getMappedPort(8000);
        String endpointUrl = String.format("http://%s:%d", host, port);
        return Map.of("quarkus.dynamodb.endpoint-override", endpointUrl);
    }

    @Override
    public void stop() {
        if (dynamodbLocal != null) {
            dynamodbLocal.stop();
        }
    }

    private void staticLogConsumer(OutputFrame outputFrame) {
        String utf8String = outputFrame.getUtf8String()
                .replaceAll("((\\r?\\n)|(\\r))$", "");
        LOGGER.append(utf8String).append('\n');
    }

}
