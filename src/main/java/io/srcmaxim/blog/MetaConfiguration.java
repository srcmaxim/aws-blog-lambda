package io.srcmaxim.blog;

import io.quarkus.arc.config.ConfigProperties;

import java.util.Map;

@ConfigProperties(prefix = "meta")
public class MetaConfiguration {

    private String buildNumber;

    private String sourceVersion;

    public Object getMeta() {
        return Map.of("buildNumber", buildNumber, "sourceVersion", sourceVersion);
    }

}
