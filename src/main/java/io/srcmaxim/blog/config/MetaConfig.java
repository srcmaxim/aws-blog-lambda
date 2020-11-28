package io.srcmaxim.blog.config;

import io.quarkus.arc.config.ConfigProperties;

import java.util.Map;

@ConfigProperties(prefix = "meta")
public class MetaConfig {

    public String buildNumber;

    public String sourceVersion;

    public Object getMeta() {
        return Map.of("buildNumber", buildNumber, "sourceVersion", sourceVersion);
    }

}
