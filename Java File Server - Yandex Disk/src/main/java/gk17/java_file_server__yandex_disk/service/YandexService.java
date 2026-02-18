package gk17.java_file_server__yandex_disk.service;

import com.fasterxml.jackson.databind.JsonNode;
import gk17.java_file_server__yandex_disk.dto.OperationStatus;
import gk17.java_file_server__yandex_disk.dto.ResourceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Service
@Slf4j
public class YandexService {
    private static final String URL = "https://cloud-api.yandex.net/v1/disk/resources";
    private static final String OPERATIONS_URL = "https://cloud-api.yandex.net/v1/disk/operations";

    @Value("${yandex.token}")
    private String token;

    private final RestTemplate restTemplate;

    public YandexService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "OAuth " + token);
        return headers;
    }

    // Скачивание файла
    public InputStream downloadFile(String path) throws IOException {
        DownloadLinkResponse downloadLink = getDownloadLink(path);

        RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(downloadLink.href()))
                .headers(createAuthHeaders())
                .build();

        ResponseEntity<byte[]> response = restTemplate.exchange(
                requestEntity,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return new ByteArrayInputStream(response.getBody());
        }
        throw new IOException("Failed to download file: " + path);
    }

    // Получение ссылки для скачивания
    private DownloadLinkResponse getDownloadLink(String path) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        ResponseEntity<DownloadLinkResponse> response = restTemplate.exchange(
                URL + "/download?path={path}",
                HttpMethod.GET,
                entity,
                DownloadLinkResponse.class,
                path
        );

        if (response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to get download link");
    }

    // Загрузка файла
    public void uploadFile(MultipartFile file, String path) throws IOException {
        UploadLinkResponse uploadLink = getUploadLink(path);

        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                uploadLink.href(),
                HttpMethod.PUT,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to upload file: " + path);
        }
    }

    // Получение ссылки для загрузки
    private UploadLinkResponse getUploadLink(String path) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        ResponseEntity<UploadLinkResponse> response = restTemplate.exchange(
                URL + "/upload?path={path}&overwrite=true",
                HttpMethod.GET,
                entity,
                UploadLinkResponse.class,
                path
        );

        if (response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to get upload link");
    }

    // Создание папки
    public boolean createDirectory(String path) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    URL + "?path={path}",
                    HttpMethod.PUT,
                    entity,
                    String.class,
                    path
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                log.warn("Folder already exists: {}", path);
                return false;
            }
            throw e;
        }
    }

    // Перемещение ресурса
    public Object moveResource(String from, String path, Boolean overwrite, Boolean forceAsync) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL + "/move")
                .queryParam("from", from)
                .queryParam("path", path);

        if (overwrite != null) {
            builder.queryParam("overwrite", overwrite);
        }
        if (forceAsync != null) {
            builder.queryParam("force_async", forceAsync);
        }

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                builder.build().toUriString(),
                HttpMethod.POST,
                entity,
                JsonNode.class
        );

        return response.getBody();
    }

    // Удаление ресурса
    public Object deleteResource(String path, Boolean permanently, Boolean forceAsync) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL)
                .queryParam("path", path);

        if (permanently != null) {
            builder.queryParam("permanently", permanently);
        }
        if (forceAsync != null) {
            builder.queryParam("force_async", forceAsync);
        }

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    builder.build().toUriString(),
                    HttpMethod.DELETE,
                    entity,
                    JsonNode.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 204) {
                return new SuccessResponse("Resource deleted successfully", null);
            }
            throw e;
        }
    }

    // Получение информации о ресурсе
    public ResourceInfo getResourceInfo(String path) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        ResponseEntity<ResourceInfo> response = restTemplate.exchange(
                URL + "?path={path}&fields=name,path,type,size,md5,mime_type,created,modified",
                HttpMethod.GET,
                entity,
                ResourceInfo.class,
                path
        );

        return response.getBody();
    }

    // Получение статуса операции
    public OperationStatus getOperationStatus(String operationId) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                OPERATIONS_URL + "/{operationId}",
                HttpMethod.GET,
                entity,
                JsonNode.class,
                operationId
        );

        JsonNode body = response.getBody();
        if (body != null) {
            return new OperationStatus(
                    body.has("href") ? body.get("href").asText() : null,
                    body.has("method") ? body.get("method").asText() : null,
                    body.has("templated") && body.get("templated").asBoolean(),
                    body.has("status") ? body.get("status").asText() : "unknown"
            );
        }
        return null;
    }
}

// Вспомогательные record классы
record DownloadLinkResponse(String href, String method, boolean templated) {}
record UploadLinkResponse(String href, String method, boolean templated) {}
record SuccessResponse(String message, Object data) {}


record Link (String href, String method, boolean templated){}
