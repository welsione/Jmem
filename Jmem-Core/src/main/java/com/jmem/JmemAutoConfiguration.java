package com.jmem;

import com.jmem.config.JmemProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "jmem", name = "enabled", havingValue = "true", matchIfMissing = false)
public class JmemAutoConfiguration {

    private final JmemProperties properties;

    public JmemAutoConfiguration(JmemProperties properties) {
        this.properties = properties;
    }
}
