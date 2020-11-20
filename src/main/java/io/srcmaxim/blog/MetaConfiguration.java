package io.srcmaxim.blog;

import io.quarkus.arc.config.ConfigProperties;

import java.util.Map;

@ConfigProperties(prefix = "meta")
public class MetaConfiguration {

    public String buildNumber;

    public String sourceVersion;

    public Object getMeta() {
        return Map.of("buildNumber", buildNumber, "sourceVersion", sourceVersion);
    }

}
