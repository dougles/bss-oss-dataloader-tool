package dataloader.http;

import com.fasterxml.jackson.databind.JsonNode;
import log.DataLogger;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import util.JsonMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BuildRequest {

    private String url;
    private JsonNode jsonBody;
    private Map<String, String> headers;

    private BuildRequest buildRequest;

    public static BuildRequest createRequest(String url) {
        return new BuildRequest(url);
    }

    private BuildRequest(String url) {
        this.url = url;
        this.headers = new HashMap<>();
    }

    public BuildRequest setBody(JsonNode jsonBody) {
        this.jsonBody = jsonBody;
        return this;
    }

    public BuildRequest setHeaders(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public BuildRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public CompletableFuture<JsonResponse> get() {
        final HttpGet get = new HttpGet(this.url);
        this.headers.forEach(get::setHeader);

        return CompletableFuture.supplyAsync(() -> this.send(get));
    }

    public CompletableFuture<JsonResponse> post() {
        final HttpPost post = new HttpPost(this.url);
        this.headers.forEach(post::setHeader);
        final Optional<StringEntity> opStringEntity = this.getStringEntity();
        opStringEntity.ifPresent(post::setEntity);

        return CompletableFuture.supplyAsync(() -> this.send(post));
    }

    public CompletableFuture<JsonResponse> patch() {
        final HttpPatch patch = new HttpPatch(this.url);
        this.headers.forEach(patch::setHeader);
        final Optional<StringEntity> opStringEntity = this.getStringEntity();
        opStringEntity.ifPresent(patch::setEntity);

        return CompletableFuture.supplyAsync(() -> this.send(patch));
    }

    public CompletableFuture<JsonResponse> delete() {
        final HttpDelete del = new HttpDelete(this.url);
        this.headers.forEach(del::setHeader);

        return CompletableFuture.supplyAsync(() -> this.send(del));
    }

    private JsonResponse send(final HttpUriRequest request) {
        JsonResponse jsonResponse = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(request);
            jsonResponse = new JsonResponse();
            jsonResponse.status = response.getStatusLine().getStatusCode();
            jsonResponse.jsonBody = JsonMapper.getJson(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
            DataLogger.error(this.getClass(), e.getMessage());
        }

        return jsonResponse;
    }

    private Optional<StringEntity> getStringEntity() {
        StringEntity stringEntity = null;
        if (this.jsonBody != null) {
            String stringJson = JsonMapper.getStringValue(this.jsonBody);
            try {
                stringEntity = new StringEntity(stringJson);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        return Optional.ofNullable(stringEntity);
    }
}
