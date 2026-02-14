package gk17.java_spring_auth.utils.swagger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoOpener {
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${swagger.https:false}")
    private boolean useHttps;

    @Value("${swagger.host:localhost}")
    private String host;

    @Value("${swagger.external-port:443}")
    private int externalPort;

    @Value("${swagger.show-port:false}")
    private boolean showPort;

    @EventListener
    public void onWebServerReady(WebServerInitializedEvent event) {
        String externalProtocol = useHttps ? "https" : "http";
        String externalUrl;

        if (useHttps && !showPort) {
            externalUrl = String.format("%s://%s%s/swagger-ui/index.html",
                    externalProtocol, host, contextPath);
        } else {
            // Для локальной разработки: с портом
            externalUrl = String.format("%s://%s:%d%s/swagger-ui/index.html",
                    externalProtocol, host, externalPort, contextPath);
        }
        openBrowser(externalUrl);
    }

    private void openBrowser(String url) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            } else {
                log.info("Не удалось автоматически открыть браузер. Откройте вручную: {}", url);
            }
        } catch (Exception e) {
            log.warn("Не удалось открыть браузер: {}", e.getMessage());
        }
    }
}