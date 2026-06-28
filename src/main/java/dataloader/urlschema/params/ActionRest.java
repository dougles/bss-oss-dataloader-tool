package dataloader.urlschema.params;

public enum ActionRest {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    GET("get");

    private String action;

    ActionRest(String name) {
        this.action = name;
    }
    public String getValue() {
        return action;
    }
}
