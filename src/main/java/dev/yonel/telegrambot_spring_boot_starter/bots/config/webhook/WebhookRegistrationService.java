
package dev.yonel.telegrambot_spring_boot_starter.bots.config.webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class WebhookRegistrationService {

    static final String API_URL = "https://api.telegram.org/bot";
    @Value("${app.url}")
    private String appUrl;

    private final RestClient restClient;

    public WebhookRegistrationService(){
        this.restClient = RestClient.builder()
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Constructor para testing o inyección
     */
    public WebhookRegistrationService(RestClient restClient) {
        this.restClient = restClient;
    }

    public void registerWebhook(String token, String path){
        String apiUrl = API_URL + token + "/setWebhook";
        String webhookUrl = appUrl + path;

        Map<String, String> requestBody = Map.of("url", webhookUrl);

        try {
            restClient.post()
                    .uri(apiUrl)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Webhook '{}' has been enabled", webhookUrl);
        }catch (Exception e){
            log.error("Webhook '{}' couldn't be enabled: {}", webhookUrl, e.getMessage());
        }
    }
}
