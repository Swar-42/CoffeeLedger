public enum CoffeeTopic {
    PERSON ("person"), ORDER ("individual order"), GROUP_ORDER ("group order");

    private final String name;

    private CoffeeTopic(String name) {
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
}
