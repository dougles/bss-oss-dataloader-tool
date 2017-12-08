package dataloader.urlschema.params;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private String method;

    HttpMethod(String name) {
        this.method = name;
    }

    public String getMethod() {
        return method;
    }
}
