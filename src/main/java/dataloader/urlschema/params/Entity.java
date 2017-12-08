package dataloader.urlschema.params;

public enum Entity {
    USER("user"),
    USER_ROLE("userRole"),
    PRODUCT_ORDER("userRole"),
    PRODUCT_SPECIFICATION("userRole");

    private String entity;

    Entity(String name) {
        this.entity = name;
    }

    public String entity() {
        return entity;
    }
}
