package io.srcmaxim.blog.config;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class MetaConfigTest {

    @Inject
    MetaConfig metaConfig;

    @Test
    void getMeta_WhenConfigSet_ThenReturnValues() {
        assertThat(metaConfig.getMeta()).isEqualTo(Map.of(
                "buildNumber", "BUILD_NUMBER",
                "sourceVersion", "SOURCE_VERSION"
        ));
        assertThat(metaConfig.buildNumber).isEqualTo("BUILD_NUMBER");
        assertThat(metaConfig.sourceVersion).isEqualTo("SOURCE_VERSION");
    }

}
