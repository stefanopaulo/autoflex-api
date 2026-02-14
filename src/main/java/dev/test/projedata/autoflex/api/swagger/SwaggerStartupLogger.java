package dev.test.projedata.autoflex.api.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class SwaggerStartupLogger {

    private static final Logger log = LoggerFactory.getLogger(SwaggerStartupLogger.class);

    @Value("${server.port:8080}")
    private int port;

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        log.info("🚀 Swagger available in: http://localhost:{}/swagger-ui/index.html", port);
    }
}