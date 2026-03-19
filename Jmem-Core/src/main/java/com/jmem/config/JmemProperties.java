package com.jmem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jmem")
public class JmemProperties {

    private boolean enabled = false;
    private String provider = "default";
}
