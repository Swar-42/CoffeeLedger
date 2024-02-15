package topics;
public enum TopicType {
    PERSON ("person"), ORDER ("individual order"), GROUP_ORDER ("group order");

    private final String name;

    private TopicType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public String plural() {
        switch (this) {
            case PERSON:
                return "people";
            case ORDER:
                return "individual orders";
            case GROUP_ORDER:
                return "group orders";
            default:
                return this.toString();
        }
    }

    public String tableName() {
        switch (this) {
            case PERSON:
                return "people";
            case ORDER:
                return "orders";
            case GROUP_ORDER:
                return "group_orders";
        }
        throw new IllegalStateException("Invalid topic type " + name);
    }
}
